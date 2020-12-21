package screenremotecontrol.client;

import com.luciad.imageio.webp.WebPReadParam;
import com.luciad.imageio.webp.WebPWriteParam;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import screenremotecontrol.client.bean.ImageData;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

/**
 * 工具类
 *
 * @Author lrh 2020/9/23 17:27
 */
public class Util {

    /**
     * 分割图片并给图片进行编号
     * @Author lrh 2020/9/28 12:15
     */
    public static Map<Integer,ImageData> splitImageAndNum(int screenWidth, int screenHeight, int marginLeft, BufferedImage sourceBufferedImage){
        Map<Integer,ImageData> dataMap = new HashMap<>();
        int width_interval = 10; //宽度分成10份
        int height_interval = 4; //高度分成2份
        int width = screenWidth / width_interval;  //图片宽度
        int height = screenHeight / height_interval; //图片高度
        int x = marginLeft; //图片x坐标
        int y = 0; //图片y坐标
        //分割图片
        BufferedImage newBufferedImage = null;
        int index = 0; //图片编号
        for (int i = 0; i < height_interval; i++) {
            for (int j = 0; j < width_interval; j++) {
                dataMap.put(index,new ImageData(null,x,y,height,width,sourceBufferedImage.getSubimage(x, y, width, height),index,screenWidth,screenHeight));
                System.out.println("分割图片位置[number="+index+",x="+x+",y="+y+",width="+width+",height="+height+"]");
                x += width;
                if(j+1 >= width_interval){
                    x = marginLeft;
                }
                index ++;
            }
            y+= height;
        }
        return dataMap;
    }

    /**
     * 分割图片并给图片进行编号
     * @Author lrh 2020/9/28 12:15
     */
    public static Map<Integer,ImageData> splitImageAndNum(int screenWidth, int screenHeight, int marginLeft){
        Map<Integer,ImageData> dataMap = new HashMap<>();
        int width_interval = 10; //宽度分成10份
        int height_interval = 2; //高度分成2份
        int width = screenWidth / width_interval;  //图片宽度
        int height = screenHeight / height_interval; //图片高度
        int x = marginLeft; //图片x坐标
        int y = 0; //图片y坐标
        //分割图片
        BufferedImage newBufferedImage = null;
        int index = 0; //图片编号
        for (int i = 0; i < height_interval; i++) {
            for (int j = 0; j < width_interval; j++) {
                dataMap.put(index,new ImageData(null,x,y,height,width,null,index,screenWidth,screenHeight));
                System.out.println("分割图片位置[number="+index+",x="+x+",y="+y+",width="+width+",height="+height+"]");
                x += width;
                if(j+1 >= width_interval){
                    x = marginLeft;
                }
                index ++;
            }
            y+= height;
        }
        return dataMap;
    }
    /**
     * 比较前后两张图片之前是否相同，如果不相同则将之前的图片更新，否则不动
     * @Author lrh 2020/10/9 11:23
     */
    public synchronized static boolean compareImageData(int imageNumber,BufferedImage newImageData,BufferedImage beforeBufferedImage){
        int height = newImageData.getHeight();
        int width = newImageData.getWidth();
//        ImageData imageData = beforeData.get(imageNumber);
//        BufferedImage beforeBufferedImage = imageData.getBufferedImage();
        int interval = new Random().nextInt(10); //保证每次扫描的行数不一样
        int intervalX = 40 + interval; //隔行扫描
        int intervalY = 40 + interval;
        for (int x=0;x<width;x += intervalX){
            for (int y = 0;y<height;y += intervalY){
                int newRGB = newImageData.getRGB(x, y);
                int oldRGB = beforeBufferedImage.getRGB(x, y);
                if(newRGB != oldRGB){
                    System.out.println("图片前后不相同，进行替换，图片编号= "+imageNumber);
//                    imageData.setBufferedImage(newImageData);
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 比较前后两张图片之前是否相同，如果不相同则将之前的图片更新，否则不动,获取裁剪后的大小
     * @Author lrh 2020/10/9 11:23
     */
    public static boolean compareImageData2(int imageNumber,BufferedImage newBufferedImage,ImageData newImageData,Map<Integer, ImageData> beforeData){
        int height = newBufferedImage.getHeight();
        int width = newBufferedImage.getWidth();
        ImageData imageData = beforeData.get(imageNumber);
        BufferedImage beforeBufferedImage = imageData.getBufferedImage();
        int intervalX = 10; //隔行扫描
        int intervalY = 10;
        //在隔行扫描的基础上判断大概图片发生变化的位置，这样发送的数据大小会减小很多，速度会快很多
        List<Integer> list = new ArrayList<>(); //保存所有变化的行号,最后只挑选最小和最大的行号,两者相减得出变化图片的高度
        for (int x=0;x<width;x += intervalX){
            for (int y = 0;y<height;y += intervalY){
                int newRGB = newBufferedImage.getRGB(x, y);
                int oldRGB = beforeBufferedImage.getRGB(x, y);
                if(newRGB != oldRGB){
                    list.add(x); //记录开始变化的行数,并继续向下对比其它行
                    break;
                }
            }
        }
        //图片一样直接返回
        if(list.size() == 0){
            return true;
        }
        System.out.println("图片前后不相同，进行替换，图片编号= "+imageNumber);
        //对比结果进行从小到大排序
        Collections.sort(list);
        int minY = list.get(0); //最小X值
        int maxY = list.get(list.size()-1); //最大X值
        //计算高度的时候,最小值-10,最大值+10
        minY = minY - intervalX < 0 ? 0 : minY - intervalX;
        maxY = maxY + intervalX > imageData.getY()+height ? imageData.getY()+height : maxY + intervalX;
        int newHeight = maxY - minY;
        //设置裁剪后的大小
        newImageData.setMiniX(0);
        newImageData.setMiniY(minY);
        newImageData.setMiniWidth(width);
        newImageData.setMiniHeight(newHeight);
        newImageData.setBufferedImage(newBufferedImage.getSubimage(0,minY,width,newHeight));
//        替换原来的图片
        imageData.setBufferedImage(newBufferedImage);
        return false;
    }
    /**
     * 获取图像异或操作后的结果
     * @Author lrh 2020/10/9 14:44
     */
    public static BufferedImage getXorImageData(int imageNumber,BufferedImage newImageData,BufferedImage beforeBufferedImage){
        ColorModel cm = newImageData.getColorModel();
        BufferedImage image = new BufferedImage(cm,
                cm.createCompatibleWritableRaster(newImageData.getWidth(), newImageData.getHeight()),
                cm.isAlphaPremultiplied(),
                null);
        int height = newImageData.getHeight();
        int width = newImageData.getWidth();
        for (int x=0;x<width;x++){
            for (int y = 0;y<height;y++){
                int newRGB = newImageData.getRGB(x, y);
                int oldRGB = beforeBufferedImage.getRGB(x, y);
                image.setRGB(x,y,newRGB ^ oldRGB);
            }
        }
        return image;
    }
    /**   
     * 异或还原图片数据
     * @Author lrh 2020/10/9 15:00
     */
    public static BufferedImage restoreXorImageData(BufferedImage oldBufferedImage,BufferedImage xorBufferedImage){
        ColorModel cm = oldBufferedImage.getColorModel();
        BufferedImage image = new BufferedImage(cm,
                cm.createCompatibleWritableRaster(oldBufferedImage.getWidth(), oldBufferedImage.getHeight()),
                cm.isAlphaPremultiplied(),
                null);
        int height = oldBufferedImage.getHeight();
        int width = oldBufferedImage.getWidth();
        for (int x=0;x<width;x++){
            for (int y = 0;y<height;y++){
                int xorRGB = xorBufferedImage.getRGB(x, y);
                int oldRGB = oldBufferedImage.getRGB(x, y);
                image.setRGB(x,y,oldRGB ^ xorRGB);
            }
        }
        return image;
    }

    /**   
     * 通过map的key进行排序
     * @Author lrh 2020/10/12 16:59
     */
    public static Map<Integer, byte[]> sortMapByKey(Map<Integer, byte[]> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Integer, byte[]> sortMap = new TreeMap<Integer, byte[]>(
                new Comparator<Integer>() {

                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o1.compareTo(o2);
                    }});
        sortMap.putAll(map);

        return sortMap;
    }
    /**   
     * 将map中的value值转换成字节数据
     * @Author lrh 2020/10/12 17:08
     * @return
     */
    public static byte[] convertMapToBytes(Map<Integer, byte[]> map){
        Set<Map.Entry<Integer, byte[]>> entries = map.entrySet();
        Collection<byte[]> values = map.values();
        int length = 0; //数组总长度
        for (Map.Entry<Integer, byte[]> entry : entries){
            byte[] data = entry.getValue();
            length += data.length;
        }
        //通过总长度然后将数组合并到一起
        byte[] allBytes = new byte[length];
        int destPos = 0; //索引位置
        for (Map.Entry<Integer, byte[]> entry : entries){
            byte[] src = entry.getValue();
            System.arraycopy(src,0,allBytes,destPos,src.length);
            destPos += src.length;
        }
        
//        int i = new Random().nextInt(Integer.MAX_VALUE);
//        try {
//            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(allBytes));
//            ImageIO.write(bufferedImage,"jpg",new File("C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\"+i+".jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println("数据总子节数="+allBytes.length);
        return allBytes;
    }

    public static void main(String[] args) throws Exception {


        /*File in_file = new File("C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\1.jpg");
        BufferedImage bufferedImage = ImageIO.read(in_file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //压缩
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);
        param.setQuality(0.35f, false);
        encoder.setJPEGEncodeParam(param);
        encoder.encode(bufferedImage);
        System.out.println("压缩之后大小="+out.toByteArray().length/1024);

        //解压缩
        byte[] buf = out.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(buf);
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
        BufferedImage image = decoder.decodeAsBufferedImage();
        ImageIO.write(image,"jpg",new File("C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\2.jpg"));
*/
        //先把原图片缩小成原来的90%大小，质量为60%
//        Robot robot = new Robot();
//        BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(0, 0, 192, 540));
//        ImageIO.write(screenCapture,"jpg",new File("C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\1.jpg"));
////        BufferedImage bufferedImage1 = Thumbnails.of(screenCapture).scale(0.9f).outputQuality(1f).outputFormat("gif").asBufferedImage();
//        byte[] bytes = bufferedImageTobytes(screenCapture, 0.3f);
//        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
//        ImageIO.write(image,"jpg",new File("C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\2.jpg"));


        // 字符串超过一定的长度
        String str = "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";
        System.out.println("原始字符串大小="+str.length());
//        List<Integer> integers = LZWcompress(str);
        String s1 = LZWcompress2(str);
        System.out.println(s1);
        String s = zipString(str.getBytes());
        System.out.println(s.length());
//        System.out.println("list="+integers.size());
//        String s = LZWdecompress(integers);
//        System.out.println("还原后的字符串： "+s);
//        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(s.getBytes()));
//        ImageIO.write(bufferedImage,"jpg",new File("C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\test.jpg"));

        //编码
//        String inputJpgPath = "C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\1.jpg";
//        String outputWebpPath = "C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\2.webp";
//        Robot robot = new Robot();
//        BufferedImage screenCapture = robot.createScreenCapture(new Rectangle(0, 0, 192, 540));
//        ImageIO.write(screenCapture,"jpg",new File(inputJpgPath));
//        byte[] bytes = webpEncode(screenCapture,0.3f);
//        System.out.println(bytes.length);

        //解码
//        String inputWebpPath = "C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\2.webp";
//        String outputJpgPath = "C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\3.jpg";
//        FileInputStream in = new FileInputStream(new File(inputWebpPath));
//        ByteArrayOutputStream arry = new ByteArrayOutputStream();
//        BufferedImage image = ImageIO.read(in);
//        ImageIO.write(image,"webp",arry);
//
//        BufferedImage image2 = webpDecode(arry.toByteArray());
//        ImageIO.write(image2, "jpg", new File(outputJpgPath));

    }

    /**
     * 压缩
     */
    public static String zipString(byte[] data) {
        /**
         *     https://www.yiibai.com/javazip/javazip_deflater.html#article-start
         *     0 ~ 9 压缩等级 低到高
         *     public static final int BEST_COMPRESSION = 9;            最佳压缩的压缩级别。
         *     public static final int BEST_SPEED = 1;                  压缩级别最快的压缩。
         *     public static final int DEFAULT_COMPRESSION = -1;        默认压缩级别。
         *     public static final int DEFAULT_STRATEGY = 0;            默认压缩策略。
         *     public static final int DEFLATED = 8;                    压缩算法的压缩方法(目前唯一支持的压缩方法)。
         *     public static final int FILTERED = 1;                    压缩策略最适用于大部分数值较小且数据分布随机分布的数据。
         *     public static final int FULL_FLUSH = 3;                  压缩刷新模式，用于清除所有待处理的输出并重置拆卸器。
         *     public static final int HUFFMAN_ONLY = 2;                仅用于霍夫曼编码的压缩策略。
         *     public static final int NO_COMPRESSION = 0;              不压缩的压缩级别。
         *     public static final int NO_FLUSH = 0;                    用于实现最佳压缩结果的压缩刷新模式。
         *     public static final int SYNC_FLUSH = 2;                  用于清除所有未决输出的压缩刷新模式; 可能会降低某些压缩算法的压缩率。
         */

        //使用指定的压缩级别创建一个新的压缩器。
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        //设置压缩输入数据。
        deflater.setInput(data);
        //当被调用时，表示压缩应该以输入缓冲区的当前内容结束。
        deflater.finish();
        final byte[] bytes = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        while (!deflater.finished()) {
            //压缩输入数据并用压缩数据填充指定的缓冲区。
            int length = deflater.deflate(bytes);
            outputStream.write(bytes, 0, length);
        }
        //关闭压缩器并丢弃任何未处理的输入。
        deflater.end();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static byte[] zipString2(byte[] data) {
        /**
         *     https://www.yiibai.com/javazip/javazip_deflater.html#article-start
         *     0 ~ 9 压缩等级 低到高
         *     public static final int BEST_COMPRESSION = 9;            最佳压缩的压缩级别。
         *     public static final int BEST_SPEED = 1;                  压缩级别最快的压缩。
         *     public static final int DEFAULT_COMPRESSION = -1;        默认压缩级别。
         *     public static final int DEFAULT_STRATEGY = 0;            默认压缩策略。
         *     public static final int DEFLATED = 8;                    压缩算法的压缩方法(目前唯一支持的压缩方法)。
         *     public static final int FILTERED = 1;                    压缩策略最适用于大部分数值较小且数据分布随机分布的数据。
         *     public static final int FULL_FLUSH = 3;                  压缩刷新模式，用于清除所有待处理的输出并重置拆卸器。
         *     public static final int HUFFMAN_ONLY = 2;                仅用于霍夫曼编码的压缩策略。
         *     public static final int NO_COMPRESSION = 0;              不压缩的压缩级别。
         *     public static final int NO_FLUSH = 0;                    用于实现最佳压缩结果的压缩刷新模式。
         *     public static final int SYNC_FLUSH = 2;                  用于清除所有未决输出的压缩刷新模式; 可能会降低某些压缩算法的压缩率。
         */

        //使用指定的压缩级别创建一个新的压缩器。
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        //设置压缩输入数据。
        deflater.setInput(data);
        //当被调用时，表示压缩应该以输入缓冲区的当前内容结束。
        deflater.finish();
        final byte[] bytes = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        while (!deflater.finished()) {
            //压缩输入数据并用压缩数据填充指定的缓冲区。
            int length = deflater.deflate(bytes);
            outputStream.write(bytes, 0, length);
        }
        //关闭压缩器并丢弃任何未处理的输入。
        deflater.end();
        return outputStream.toByteArray();
    }
    /**
     * 解压缩
     */
    public static byte[] unzipString(String zipString) {
        byte[] decode = Base64.getDecoder().decode(zipString);
        //创建一个新的解压缩器  https://www.yiibai.com/javazip/javazip_inflater.html
        Inflater inflater = new Inflater();
        //设置解压缩的输入数据。
        inflater.setInput(decode);
        final byte[] bytes = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        try {
            //finished() 如果已到达压缩数据流的末尾，则返回true。
            while (!inflater.finished()) {
                //将字节解压缩到指定的缓冲区中。
                int length = inflater.inflate(bytes);
                outputStream.write(bytes, 0, length);
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        } finally {
            //关闭解压缩器并丢弃任何未处理的输入。
            inflater.end();
        }
        return outputStream.toByteArray();
    }

    public static byte[] unzipString2(byte[] decode) {
        //创建一个新的解压缩器  https://www.yiibai.com/javazip/javazip_inflater.html
        Inflater inflater = new Inflater();
        //设置解压缩的输入数据。
        inflater.setInput(decode);
        final byte[] bytes = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        try {
            //finished() 如果已到达压缩数据流的末尾，则返回true。
            while (!inflater.finished()) {
                //将字节解压缩到指定的缓冲区中。
                int length = inflater.inflate(bytes);
                outputStream.write(bytes, 0, length);
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        } finally {
            //关闭解压缩器并丢弃任何未处理的输入。
            inflater.end();
        }
        return outputStream.toByteArray();
    }
    /**
     * 将图片转换成webp格式
     * @param qualit 0-1 之间，表示图片质量，默认0.3
     * @Author lrh 2020/10/22 15:40
     */
    public static byte[] webpEncode2(BufferedImage bufferedImage,float qualit) throws IOException {
        ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByMIMEType("image/webp").next();
        WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
        writeParam.setCompressionMode(1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream m = new MemoryCacheImageOutputStream(out);
//        writer.setOutput(m);
        writer.setOutput(new FileImageOutputStream(new File("C:\\Users\\MACHENIKE\\Desktop\\新建文件夹\\"+new Random().nextInt(Integer.MAX_VALUE)+".webp")));
        writer.write((IIOMetadata)null, new IIOImage(bufferedImage, (List)null, (IIOMetadata)null), writeParam);
        m.flush();//需要调用这个方法，将内存中的数据刷新到输出流中，否则输出流没有数据
        return out.toByteArray();
    }
    /**   
     * 将图片转换成webp格式
     * @param qualit 0-1 之间，表示图片质量，默认0.3
     * @Author lrh 2020/10/22 15:40
     */
    public static byte[] webpEncode(BufferedImage bufferedImage,float qualit){
        ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByMIMEType("image/webp").next();
        WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
        writeParam.setCompressionMode(MODE_EXPLICIT); //压缩模式，不能变
        writeParam.setCompressionType("Lossy"); //Lossless 无损压缩，Lossy 有损压缩
        writeParam.setCompressionQuality(qualit); //压缩质量
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream m = new MemoryCacheImageOutputStream(outputStream);
        writer.setOutput(m);
        try {
           writer.write((IIOMetadata)null, new IIOImage(bufferedImage, (List)null, (IIOMetadata)null), writeParam);
           m.flush();//需要调用这个方法，将内存中的数据刷新到输出流中，否则输出流没有数据
           return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**   
     * 将webp字节数组解码
     * @Author lrh 2020/10/27 13:51
     */
    public static BufferedImage webpDecode(byte[] bytes){
        ImageReader reader = (ImageReader)ImageIO.getImageReadersByMIMEType("image/webp").next();
        WebPReadParam readParam = new WebPReadParam();
        readParam.setBypassFiltering(true);
        MemoryCacheImageInputStream in = new MemoryCacheImageInputStream(new ByteArrayInputStream(bytes));
        reader.setInput(in);
        BufferedImage image = null;
        try {
            image = reader.read(0, readParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    
    

    /** Compress a string to a list of output symbols. */
    public static List<Integer> LZWcompress(String uncompressed) {
        // Build the dictionary.
        int dictSize = 256;
        Map<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char)i, i);

        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }

        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));
        return result;
    }

    public static String LZWcompress2(String uncompressed) {
        // Build the dictionary.
        int dictSize = 256;
        Map<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char)i, i);

        String w = "";
        StringBuilder builder = new StringBuilder();
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                builder.append(dictionary.get(w)).append(",");
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }

        // Output the code for w.
        if (!w.equals(""))
            builder.append(dictionary.get(w)).append(",");
        return builder.toString();
    }
    /** Decompress a list of output ks to a string. */
    public static String LZWdecompress(List<Integer> compressed) {
        // Build the dictionary.
        int dictSize = 256;
        Map<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < 256; i++)
            dictionary.put(i, "" + (char)i);

        String w = "" + (char)(int)compressed.remove(0);
        StringBuffer result = new StringBuffer(w);
        for (int k : compressed) {
            String entry;
            if (dictionary.containsKey(k))
                entry = dictionary.get(k);
            else if (k == dictSize)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Bad compressed k: " + k);

            result.append(entry);

            // Add w+entry[0] to the dictionary.
            dictionary.put(dictSize++, w + entry.charAt(0));

            w = entry;
        }
        return result.toString();
    }

    /**
     * 对图像进行压缩
     * @param image 图像
     * @return 图像数据包装类
     * @throws ImageFormatException
     * @throws IOException
     * @throws IOException
     */
    public static byte[] encodeImage(BufferedImage image){

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
        param.setQuality(0.75f, false);
        encoder.setJPEGEncodeParam(param);
        try {
            encoder.encode(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();

    }

    /**
     * byte[] 数组转换成 char[] 数组
     * @Author lrh 2020/11/10 10:49
     */
    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    /**对图像进行解压缩
     * @param ito 图像数据包装类
     * @return BufferedImage 图像
     * @throws ImageFormatException
     * @throws IOException
     * @throws IOException
     */

    public static BufferedImage decodeImage(byte[] buf){
        ByteArrayInputStream in = new ByteArrayInputStream(buf);
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
        BufferedImage image = null;
        try {
            image = decoder.decodeAsBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}
