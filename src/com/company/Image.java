//package src.com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class Image {
    //方式一：通过getRGB()方式获得像素矩阵
    public static void getPicArrayData(String path) {
        try {
            BufferedImage bimg = ImageIO.read(new File(path));
            int[][] data = new int[bimg.getWidth()][bimg.getHeight()];
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    data[i][j] = bimg.getRGB(i, j);
                    //输出一列数据比对
                    if (i == 0) {
                        String format = String.format("%x", data[i][j]);
                        System.out.println(format);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[][] convertTo2DWithoutUsingGetRGB(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb -= 16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
//        File file = new File("/Users/chenyingqing/Library/Mobile Documents/com~apple~CloudDocs/Documents/study/ThreeDays/同态加密/bgn_java/LenaRGB.bmp");
        String path = "/Users/chenyingqing/Library/Mobile Documents/com~apple~CloudDocs/Documents/study/ThreeDays/同态加密/bgn_java/LenaRGB.bmp";

        //方式一
//        getPicArrayData(path);
        //方式二

        System.out.println("Method 2");
        int[][] ints = convertTo2DWithoutUsingGetRGB(path);
        for (int i = 0; i < ints.length; i++) {
            for (int j = 0; j < ints[i].length; j++) {
                //只打印一列数据
                if (i == 0) {
                    String format = String.format("%d", ints[i][j]);
                    System.out.println(format);
                }
            }
        }
    }
}
