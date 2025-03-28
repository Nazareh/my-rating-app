package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.opencsv.bean.CsvToBeanBuilder;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.playerMatchService.PlayerMatchService;
import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;

@Service
@RequiredArgsConstructor
@Log4j2
public class PlayerService {
    private final PlayerRepository repository;
    private final FirebaseAuth firebaseAuth;
    private final PlayerMapper mapper;
    private final PlayerMatchService playerMatchService;
    private final AuthenticationFacade authenticationFacade;

    private Map<String, PlayerDto> playersCache = new HashMap<>();

    public Player findById(String id) {
        return repository.findById(new ObjectId(id)).orElseThrow();
    }

    public Optional<Player> findByUserUidOrOnboard(String userUid) {
        var player = repository.findByUserUid(userUid);

        if (player.isEmpty()) {
            player = Optional.of(createNewPlayerFromUserUid(userUid));
        }

        return player;
    }

    public final Player findByEmailOrCreate(String email) {
        return repository.findByEmail(email)
                .or(() -> Optional.of(
                        repository
                                .save(new Player().setEmail(email)
                                        .setName(email.contains("@") ? email.split("@")[0] : email))))
                .get();
    }

    public void eraseAllRatings() {
        repository.findAll().forEach(
                player ->
                        repository.save(player.setGamesLost(0).setGamesWon(0).setMatchesWon(0).setMatchesLost(0).setRatings(new HashMap<>())));
    }

    Set<PlayerDto> registerPlayersFromCsv(InputStream inputStream) {
        return new CsvToBeanBuilder<RegisterPlayerDto>(new InputStreamReader(inputStream))
                .withType(RegisterPlayerDto.class)
                .build().parse().stream()
                .map(mapper::toPlayer)
                .map(repository::save)
                .map(mapper::toPlayerDto)
                .collect(Collectors.toSet());
    }

    List<PlayerDto> getAllPlayers() {
        if (playersCache.isEmpty()) {
            playersCache = repository.findAll().stream()
                    .map(mapper::toPlayerDto)
                    .collect(Collectors.toMap(PlayerDto::id, p -> p));
        }
        return playersCache.values().stream()
                .sorted(Comparator.comparing(
                        p -> p.lastRatings() != null && p.lastRatings().containsKey(RatingType.ELO)
                                ? p.lastRatings().get(RatingType.ELO).getValue()
                                : 0,
                        Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    PlayerDto getPlayerById(String id) {
        var player = findById(id);

       return  mapper.toPlayerDto(player,
                player.getUserUid()!= null && player.getUserUid().equals(authenticationFacade.getUserUid())
                       ? playerMatchService.getMatchesByPlayer(player.getId(), Optional.of(MatchStatus.PENDING))
                       : Collections.emptyList());

    }

    public void updatePlayerStats(MatchPlayer matchPlayer, Match match) {
        playersCache.clear();

        var player = findById(matchPlayer.getId());

        if (getWinnerTeam(match).orElseThrow() == matchPlayer.getTeam())
            player.setMatchesWon(player.getMatchesWon() + 1);
        else
            player.setMatchesLost(player.getMatchesLost() + 1);

        int team1GamesWon = match.getScores().stream().map(SetScore::getTeam1).reduce(0, Integer::sum);
        int team2GamesWon = match.getScores().stream().map(SetScore::getTeam2).reduce(0, Integer::sum);

        if (matchPlayer.getTeam() == Team.TEAM_1) {
            player.setGamesWon(player.getGamesWon() + team1GamesWon);
            player.setGamesLost(player.getGamesLost() + team2GamesWon);
        } else {
            player.setGamesWon(player.getGamesWon() + team2GamesWon);
            player.setGamesLost(player.getGamesLost() + team1GamesWon);
        }

        repository.save(player);

    }

    public boolean isValidEmail(String email) {
        final String EMAIL_REGEX = "^(?!\\.)(?!.*\\.\\.)[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    PlayerDto onboardPlayer(String userUid) throws FirebaseAuthException {
        return mapper.toPlayerDto(createNewPlayerFromUserUid(userUid));
    }

    private Player createNewPlayerFromUserUid(String userUid) {
        playersCache.clear();
        UserRecord userRecord = null;
        try {
            userRecord = firebaseAuth.getUser(userUid);
        } catch (FirebaseAuthException ex) {

            log.error(ex.getMessage());
            throw new RuntimeException("It not possible to find user with UID " + userUid);

        }
        var existingPlayer = repository.findByEmail(userRecord.getEmail());
        return repository.save(
                existingPlayer.isPresent()
                        ? existingPlayer.get().setUserUid(userUid).setName(userRecord.getDisplayName())
                        : mapper.toPlayer(userRecord)
        );
    }

    public void removeMatchFromPendingMatches(Match match) {
        match.getPlayers().stream()
                .map(MatchPlayer::getId)
                .map(ObjectId::new)
                .map(repository::findById)
                .map(Optional::orElseThrow)
                .forEach(repository::save);

    }
}
