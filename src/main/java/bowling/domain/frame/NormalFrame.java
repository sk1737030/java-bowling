package bowling.domain.frame;

import bowling.domain.frame.info.FrameInfo;
import bowling.domain.frame.info.NormalFrameInfo;
import bowling.domain.pins.Pins;
import bowling.domain.pins.Status;
import bowling.domain.score.Score;
import java.util.Optional;

public class NormalFrame implements Frame {

    private final NormalFrameInfo frameInfo;
    private final Pins pins;

    private NormalFrame(NormalFrameInfo normalFrameInfo, Pins pins) {
        this.frameInfo = normalFrameInfo;
        this.pins = pins;
    }

    public static NormalFrame create() {
        return of(NormalFrameInfo.create(), Pins.create());
    }

    public static NormalFrame of(NormalFrameInfo normalFrameInfo, Pins pins) {
        return new NormalFrame(normalFrameInfo, pins);
    }


    @Override
    public void roll(int downPins) {
        pins.roll(Score.from(downPins), frameInfo);
    }

    @Override
    public Optional<Frame> nextRound() {
        if (isLastFrame()) {
            return Optional.of(FinalFrame.create());
        }

        if (isCurrentRoundEnd()) {
            return Optional.of(of(frameInfo.nextFrame(), Pins.create()));
        }

        return Optional.of(of(frameInfo.nextRound(), Pins.of(pins, Status.READY)));
    }

    @Override
    public boolean hasNextRound() {
        return !frameInfo.isLastRound() || pins.isReady();
    }

    private boolean isLastFrame() {
        return frameInfo.isEndFrame() && isCurrentRoundEnd();
    }

    private boolean isCurrentRoundEnd() {
        return pins.isAllDown() || frameInfo.isLastRound();
    }

    @Override
    public boolean isFrameEnd(int givenFrame) {
        return frameInfo.isAfterFrame(givenFrame);
    }

    @Override
    public Optional<Score> calcScore(Frames playerFrames) {
        if (pins.isStrike()) {
            return Optional.of(playerFrames.calcStrike(this));
        }

        if (pins.isSpare()) {
            return Optional.of(playerFrames.calcSpare(this));
        }

        if (!hasNextRound()) {
            return Optional.of(playerFrames.calcMiss(this));
        }

        return Optional.empty();
    }

    @Override
    public Score numberOfDownedPins() {
        return pins.numberOfPinDowns();
    }

    @Override
    public FrameInfo frameInfo() {
        return frameInfo;
    }

    @Override
    public Status pinStatus() {
        return pins.status();
    }

}
