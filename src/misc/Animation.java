package misc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Animation {

    public List<BufferedImage> sprites;
    public int currentSpriteIndex = 0;
    private final int updateTime;
    private long lastUpdateTime = 0;

    public Animation(int updateTime) {
        this.updateTime = updateTime;
        this.sprites = new ArrayList<>();
    }

    // updating sprite every set milliseconds
    public void updateSprite() {
        if (System.currentTimeMillis() - lastUpdateTime >= updateTime) {
            currentSpriteIndex++;
            if (currentSpriteIndex >= sprites.size()) {
                currentSpriteIndex = 0;
            }
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    public void addSprite(BufferedImage sprite) {
        sprites.add(sprite);
    }

    public BufferedImage getSprite() {
        if (currentSpriteIndex >= sprites.size()) {
            return sprites.get(sprites.size() - 1);
        }
        if (!sprites.isEmpty()) {
            return sprites.get(currentSpriteIndex);
        }
        return null;
    }

}
