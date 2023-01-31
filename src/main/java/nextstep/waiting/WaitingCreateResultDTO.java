package nextstep.waiting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WaitingCreateResultDTO {
    private final boolean isCreated;
    private final Long id;
}
