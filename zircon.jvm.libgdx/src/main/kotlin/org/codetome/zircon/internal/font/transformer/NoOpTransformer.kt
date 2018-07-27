package org.codetome.zircon.internal.font.transformer

import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.codetome.zircon.api.data.Tile
import org.codetome.zircon.api.font.FontTextureRegion
import org.codetome.zircon.internal.font.FontRegionTransformer

class NoOpTransformer : FontRegionTransformer<TextureRegion> {
    override fun transform(region: FontTextureRegion<TextureRegion>, tile: Tile) = region
}