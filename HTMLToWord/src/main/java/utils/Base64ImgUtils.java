package utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Base64ImgUtils {
	/**
	 * TODO:将以Base64方式编码的字符串解码为byte数组
	 * 
	 * @param encodeStr
	 *            待解码的字符串
	 * @return 解码后的byte数组
	 * @throws IOException
	 */
	public static byte[] decode(String encodeStr) throws IOException {
		byte[] bt = null;
		BASE64Decoder decoder = new BASE64Decoder();
		bt = decoder.decodeBuffer(encodeStr);
		for (int i = 0; i < bt.length; ++i) {
			if (bt[i] < 0) {// 调整异常数据
				bt[i] += 256;
			}
		}
		return bt;
	}
	
	
	/**将远程图片转成baty[]**/
	public static byte[] getUrlImg(String imgUrl){
    	if(imgUrl==null){
    		return null;
    	}
		int num = imgUrl.indexOf('/',8);//获取防盗链图片
		byte[] imgByteArray = null;
		if(num>0)
		{
		String u = imgUrl.substring(0,num);
        try {
    		URL url = new URL(imgUrl);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("referer", u);       //通过这个http头的伪装来反盗链
			InputStream imgInputStream = connection.getInputStream();
			imgByteArray = imgTobyteArray(imgInputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		 return imgByteArray;

		
	}
	
	
	@SuppressWarnings("resource")
	public static byte[] imgTobyteArray(File file){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buff = new byte[256];
			int rc=0;
			while((rc = fis.read(buff,0,256))>0){
				baos.write(buff,0,rc);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] outbyteArray = baos.toByteArray();
		return outbyteArray;
	}
	
	
	
	/**将inputStream转成byteArray**/
	public static byte[] imgTobyteArray(InputStream inputStream){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buff = new byte[256];
			int rc=0;
			while((rc = inputStream.read(buff,0,256))>0){
				baos.write(buff,0,rc);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] outbyteArray = baos.toByteArray();
		return outbyteArray;
	}
	
	/**将baty[]转为base64**/
	public static String byteArrayToBase64(byte[] byteArray){
		if(byteArray==null){
			return null;
		}
		BASE64Encoder encoder = new BASE64Encoder();
		String base64Str = encoder.encode(byteArray);
		return base64Str;
	}
	
	public static void main(String[] args) {
		String imgUrl = "http://7xrxfe.com1.z0.glb.clouddn.com/educateTeacherUpload/73516cce-47e5-4beb-a88b-ef816122eca8.gif";
		byte[] imgByteArray = getUrlImg(imgUrl);
		String a = byteArrayToBase64(imgByteArray);
		System.out.println(a);
	}
}
