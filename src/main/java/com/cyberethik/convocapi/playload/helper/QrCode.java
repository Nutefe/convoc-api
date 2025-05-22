package com.cyberethik.convocapi.playload.helper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QrCode {

//    private static final String DIR = "~/Bureau/";
    private static final String DIR = Helpers.path_file_exports;
    private static final String ext = ".png";
//    private static final String LOGO = "http://localhost:9197/web/service/downloadFile/logo_icon64x78.png";
    private static final String LOGO = Helpers.base_file_url+"downloadFile/logo_icon64x78.png";

    //    private static final String CONTENT = "some content here";
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    public static String generate(String content, String name) {
        // Create new configuration that specifies the error correction
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            // init directory
//            cleanDirectory(DIR);
            initDirectory(DIR);
            // Create a qr code with the url as content and a size of WxH px
            bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

            // Load QR image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, getMatrixConfig());

            // Load logo image
            BufferedImage overly = getOverly(LOGO);

            // Calculate the delta height and width between QR code and logo
            int deltaHeight = qrImage.getHeight() - overly.getHeight();
            int deltaWidth = qrImage.getWidth() - overly.getWidth();

            // Initialize combined image
            BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();

            // Write QR code to new image at position 0/0
            g.drawImage(qrImage, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Write logo into combine image at position (deltaWidth / 2) and
            // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
            // the same space for the logo to be centered
            g.drawImage(overly, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));

            // Write combined image as PNG to OutputStream
            ImageIO.write(combined, "png", os);
            // Store Image
//            String fileName = name;
            String fileName = Helpers.generat();
//            Files.copy( new ByteArrayInputStream(os.toByteArray()), Paths.get(DIR + generateRandoTitle(new Random(), 9) +ext), StandardCopyOption.REPLACE_EXISTING);
            Files.copy( new ByteArrayInputStream(os.toByteArray()), Paths.get(DIR + fileName +ext), StandardCopyOption.REPLACE_EXISTING);
            return Paths.get(DIR + fileName +ext).toString();
        } catch (WriterException e) {
            e.printStackTrace();
            return "";
            //LOG.error("WriterException occured", e);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
            //LOG.error("IOException occured", e);
        }
    }

    private static BufferedImage getOverly(String LOGO) throws IOException {
        URL url = new URL(LOGO);
        return ImageIO.read(url);
    }

    private static void initDirectory(String DIR) throws IOException {
        Files.createDirectories(Paths.get(DIR));
    }

    private static void cleanDirectory(String DIR) {
        try {
            Files.walk(Paths.get(DIR), FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            // Directory does not exist, Do nothing
            System.out.println(e);
        }
    }

    private static MatrixToImageConfig getMatrixConfig() {
        // ARGB Colors
        // Check Colors ENUM
        return new MatrixToImageConfig(Colors.BLUE.getArgb(), Colors.WHITE.getArgb());
    }

    private static String generateRandoTitle(Random random, int length) {
        return random.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public enum Colors {

//        BLUE(0xFF40BAD0),
        BLUE(0xFF2A3179),
        RED(0xFFE91C43),
        PURPLE(0xFF8A4F9E),
        ORANGE(0xFFF4B13D),
        WHITE(0xFFFFFFFF),
        BLACK(0xFF000000);

        private final int argb;

        Colors(final int argb){
            this.argb = argb;
        }

        public int getArgb(){
            return argb;
        }
    }
}
