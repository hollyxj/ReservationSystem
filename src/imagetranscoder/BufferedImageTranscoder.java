package imagetranscoder;

import java.awt.image.BufferedImage;

class BufferedImageTranscoder extends ImageTranscoder {
  @Override
  public BufferedImage createImage(int w, int h)
  {
    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    return bi;
  }

  @Override
  public void writeImage(BufferedImage img, TranscoderOutput output)
  {
    this.img = img;
  }

  public BufferedImage getBufferedImage()
  {
    return img;
  }
  private BufferedImage img = null;
}
