package game_object;

import manager.SoundManager;
import misc.Animation;
import misc.Controls;
import misc.DinoState;

import java.awt.*;
import java.awt.image.BufferedImage;

import static user_interface.GameScreen.*;
import static util.Resource.getImage;

public class Dino {

    // values to subtract from x, y, width, height to get accurate hitbox
    private static final int[] HITBOX_RUN = {12, 26, -32, -42};
    private static final int[] HITBOX_DOWN_RUN = {24, 20, -60, -30};

    public static final double X = 120;

    Controls controls;

    private double maxY;
    private final double highJumpMaxY;
    private final double lowJumpMaxY;

    private double y;
    private double speedY = 0;

    private DinoState dinoState;
    private final BufferedImage dinoJump;
    public final Animation dinoDead;
    private final Animation dinoRun;
    private final Animation dinoDownRun;
    private final SoundManager jumpSound;

    public Dino(Controls controls) {
        this.controls = controls;
        dinoRun = new Animation(150);
        dinoRun.addSprite(getImage("resources/duck-run-1.png"));
        dinoRun.addSprite(getImage("resources/duck-run-2.png"));
        dinoRun.addSprite(getImage("resources/duck-run-3.png"));
        dinoRun.addSprite(getImage("resources/duck-run-4.png"));

        dinoDownRun = new Animation(150);
        dinoDownRun.addSprite(getImage("resources/duck-down-run-1.png"));
        dinoDownRun.addSprite(getImage("resources/duck-down-run-2.png"));
        dinoDownRun.addSprite(getImage("resources/duck-down-run-3.png"));
        dinoDownRun.addSprite(getImage("resources/duck-down-run-4.png"));

        dinoDead = new Animation(500);
        dinoDead.addSprite(getImage("resources/duck-dead-1.png"));
        dinoDead.addSprite(getImage("resources/duck-dead-2.png"));
        dinoDead.addSprite(getImage("resources/duck-dead-3.png"));
        dinoDead.addSprite(getImage("resources/duck-dead-4.png"));
        dinoDead.addSprite(getImage("resources/duck-dead-5.png"));

        dinoJump = getImage("resources/duck-jump.png");

        jumpSound = new SoundManager("resources/jump.wav");
        jumpSound.startThread();
        y = GROUND_Y - dinoJump.getHeight();
        maxY = y;
        highJumpMaxY = setJumpMaxY(GRAVITY);
        lowJumpMaxY = setJumpMaxY(GRAVITY + GRAVITY / 2);
        dinoState = DinoState.DINO_JUMP;
    }

    public DinoState getDinoState() {
        return dinoState;
    }

    public void setDinoState(DinoState dinoState) {
        this.dinoState = dinoState;
    }

    public double setJumpMaxY(double gravity) {
        speedY = SPEED_Y;
        y += speedY;
        double jumpMaxY = y;
        while (true) {
            speedY += gravity;
            y += speedY;
            if (y < jumpMaxY) {
                jumpMaxY = y;
            }
            if (y + speedY >= GROUND_Y - dinoRun.getSprite().getHeight()) {
                speedY = 0;
                y = GROUND_Y - dinoRun.getSprite().getHeight();
                break;
            }
        }
        return jumpMaxY;
    }

    public Rectangle getHitbox() {
        return switch (dinoState) {
            case DINO_RUN, DINO_JUMP, DINO_DEAD -> new Rectangle(
                    (int) X + HITBOX_RUN[0],
                    (int) y + HITBOX_RUN[1],
                    dinoDead.getSprite().getWidth() + HITBOX_RUN[2],
                    dinoDead.getSprite().getHeight() + HITBOX_RUN[3]
            );
            case DINO_DOWN_RUN -> new Rectangle(
                    (int) X + HITBOX_DOWN_RUN[0],
                    (int) y + HITBOX_DOWN_RUN[1],
                    dinoDownRun.getSprite().getWidth() + HITBOX_DOWN_RUN[2],
                    dinoDownRun.getSprite().getHeight() + HITBOX_DOWN_RUN[3]
            );
        };
    }

    public void updatePosition() {
        if (y < maxY) {
            maxY = y;
        }

        dinoRun.updateSprite();
        dinoDownRun.updateSprite();
        dinoDead.updateSprite();

        switch (dinoState) {
            case DINO_RUN:
                y = GROUND_Y - dinoRun.getSprite().getHeight();
                maxY = y;
                break;
            case DINO_DOWN_RUN:
                y = GROUND_Y - dinoDownRun.getSprite().getHeight() + 5;
                break;
            case DINO_JUMP:
                if (y + speedY >= GROUND_Y - dinoRun.getSprite().getHeight()) {
                    speedY = 0;
                    y = GROUND_Y - dinoRun.getSprite().getHeight();
                    dinoState = DinoState.DINO_RUN;
                }
                else if (controls.isPressedUp()) {
                    speedY += GRAVITY;
                    y += speedY;
                }
                else {
                    if (maxY <= lowJumpMaxY - (lowJumpMaxY - highJumpMaxY) / 2)
                        speedY += GRAVITY;
                    else
                        speedY += GRAVITY + GRAVITY / 2;
                    if (controls.isPressedDown())
                        speedY += GRAVITY;
                    y += speedY;
                }
                break;
            default:
                break;
        }
    }

    public void jump() {
        if (y == GROUND_Y - dinoRun.getSprite().getHeight()) {
            jumpSound.play();
            speedY = SPEED_Y;
            y += speedY;
        }
    }

    public void resetDino() {
        y = GROUND_Y - dinoJump.getHeight();
        dinoState = DinoState.DINO_RUN;
    }

    public void dinoGameOver() {
        if (y > GROUND_Y - dinoDead.getSprite().getHeight()) {
            y = GROUND_Y - dinoDead.getSprite().getHeight();
        }
        dinoState = DinoState.DINO_DEAD;
    }

    public void draw(Graphics g) {
        switch (dinoState) {
            case DINO_RUN:
                g.drawImage(dinoRun.getSprite(), (int) X, (int) y, null);
                break;
            case DINO_DOWN_RUN:
                g.drawImage(dinoDownRun.getSprite(), (int) X, (int) y, null);
                break;
            case DINO_JUMP:
                g.drawImage(dinoJump, (int) X, (int) y, null);
                break;
            case DINO_DEAD:
                dinoDead.currentSpriteIndex++;
                g.drawImage(dinoDead.getSprite(), (int) X, (int) y, null);
                break;
            default:
                break;
        }
    }

    public void drawHitbox(Graphics g) {
        g.setColor(Color.GREEN);
        g.drawRect(getHitbox().x, getHitbox().y, getHitbox().width, getHitbox().height);
    }

}
