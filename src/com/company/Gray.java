//package src.com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Gray {
    public static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

    public static int[] GrayImage(String filePath, String picPath) throws IOException{
//        read image into gray scale
//        GrayImage[0] = width;
//        GrayImage[1] = length;
        BufferedImage bufferedImage
                = ImageIO.read(new File(picPath));
//                = ImageIO.read(new File(System.getProperty(picPath)));
        BufferedImage grayImage =
                new BufferedImage(bufferedImage.getWidth(),
                        bufferedImage.getHeight(),
                        bufferedImage.getType());
        int width = bufferedImage.getWidth();
        int length = bufferedImage.getHeight();
        int numCount = width * length;
        int[] GrayImage = new int[numCount+2];
        GrayImage[0] = width;
        GrayImage[1] = length;
        int n = 2;
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                final int color = bufferedImage.getRGB(i, j);
                final int r = (color >> 16) & 0xff;
                final int g = (color >> 8) & 0xff;
                final int b = color & 0xff;
                int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);;
//                System.out.println(i + " : " + j + " " + gray);
                GrayImage[n] = gray;
                n++;
                int newPixel = colorToRGB(255, gray, gray, gray);
                grayImage.setRGB(i, j, newPixel);
            }
        }
//        File newFile = new File(filePath + "/ok.jpg");
//        ImageIO.write(grayImage, "jpg", newFile);
        return GrayImage;
    }
    public static void imgSave(int []pic,String filePath)throws IOException{
        int width = pic[0];
        int length = pic[1];
        int numCount = width * length;
        BufferedImage grayImage = new BufferedImage(width, length, 1);
        for(int i =0; i<width; i++){
            for(int j =0; j<length; j++){
                int newPixel = colorToRGB(255, pic[i+j+2], pic[i+j+2], pic[i+j+2]);
                grayImage.setRGB(i, j, newPixel);
            }
        }
        File newFile = new File(filePath + "/recover.jpg");
        ImageIO.write(grayImage, "jpg", newFile);

    }



//    public static void main(String[] args) throws IOException {
//        String filePath = "/Users/chenyingqing/Library/Mobile Documents/com~apple~CloudDocs/Documents/study/ThreeDays/同态加密/bgn_java";
//        String picPath = filePath+"/LenaRGB.bmp";
//        int[] GrayImage = GrayImage(filePath, picPath);
//        int width = GrayImage[0];
//        int height = GrayImage[1];
//        for (int i = 2; i < width*height; i++) {
//                System.out.println(GrayImage[i]);
//            }
//    }
}
