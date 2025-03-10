package io.github.enkarin.bookcrossing.utils;

import io.github.enkarin.bookcrossing.exception.UnsupportedImageTypeException;
import lombok.experimental.UtilityClass;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class ImageCompressor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCompressor.class);

    public static byte[] compressImage(final BufferedImage imageData, final int height, final int weight) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
                final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                writer.setOutput(imageOutputStream);
                final BufferedImage outputImage = Scalr.resize(imageData, weight, height);
                writer.write(null, new IIOImage(outputImage, null, null), null);
                final byte[] result = byteArrayOutputStream.toByteArray();
                writer.dispose();
                return result;
            }
        } catch (IOException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw new UnsupportedImageTypeException(exception.getMessage(), exception);
        }
    }
}
