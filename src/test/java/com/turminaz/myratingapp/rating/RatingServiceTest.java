package com.turminaz.myratingapp.rating;

import com.netflix.dgs.codegen.generated.types.SetResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RatingServiceTest {

    private static Stream<Arguments> provideSetsResultForHasTeam1WonFnc() {
        return Stream.of(
                Arguments.of(List.of(new SetResponse(6,0), new SetResponse(6,0), new SetResponse(6,0)), true),
                Arguments.of(List.of(new SetResponse(6,5)),true),
                Arguments.of(List.of(new SetResponse(6,7)),false),
                Arguments.of(List.of(new SetResponse(6,5),new SetResponse(6,5),new SetResponse(0,6)),true),
                Arguments.of(List.of(new SetResponse(6,0)),true),
                Arguments.of(List.of(new SetResponse(6,0)),true),
                Arguments.of(List.of(new SetResponse(6,0)),true),
                Arguments.of(List.of(new SetResponse(6,0)),true),
                Arguments.of(List.of(new SetResponse(6,0)),true),
                Arguments.of(List.of(new SetResponse(6,0)),true),
                Arguments.of(List.of(new SetResponse(6,0)),true),
                Arguments.of(List.of(new SetResponse(6,0)),true)

        );

    }

    @ParameterizedTest
    @MethodSource("provideSetsResultForHasTeam1WonFnc")
    void testHasTeam1Won(List<SetResponse> sets, boolean expectedResult) {

    }
}