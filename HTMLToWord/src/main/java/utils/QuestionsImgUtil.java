package utils;

import Beans.QuestionTagBean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class QuestionsImgUtil {



	/**下载远程图片转为base64**/
	public static String urlImgToBase64Data(String url){
		String base64Data = null;
		int subStringNumber = url.lastIndexOf(".");
		String suffix = url.substring(subStringNumber+1, url.length());
		String base64Mime = "data:image/"+suffix+";base64,";
		byte[] imgByteArray = Base64ImgUtils.getUrlImg(url);
		String base64Str = Base64ImgUtils.byteArrayToBase64(imgByteArray);
		base64Data = base64Mime+base64Str;
		return base64Data;
	}

	/**
	 * 
	 * @param questionTagBean
	 * 将题干中的图片img转成base64imgTag，用于将html转为docx
	 * 从第几个开始找起imgTag,
	 * @return
	 */
	public static QuestionTagBean conversionImgSrcToBase64Src(QuestionTagBean questionTagBean)throws Exception{
		Integer startNumber = questionTagBean.getNumber();
		String allQuestionTurnk = questionTagBean.getQuestionTrunk();
		int newStartNumber = questionTagBean.getNumber();
		newStartNumber = allQuestionTurnk.indexOf("<img", startNumber);
		if(newStartNumber==-1){
			return questionTagBean;
		}
		int endNumber = allQuestionTurnk.indexOf("\"/>", newStartNumber);
		String imgTag = allQuestionTurnk.substring(newStartNumber, endNumber+3);//get tag
		String imgUrl = getImgTagUrl(imgTag);
		if(imgUrl.length()>0)
		{
		String base64Src = urlImgToBase64Data(imgUrl);
		String base64ImgTag = replaceImgTagSrcToBase64(imgTag, base64Src);
		//replace the tag
		StringBuffer sb = new StringBuffer();
		sb.append(allQuestionTurnk);
		sb.delete(newStartNumber, endNumber+3);
		sb.insert(newStartNumber, base64ImgTag);
		newStartNumber = sb.indexOf(base64ImgTag, newStartNumber)+base64ImgTag.length();
		allQuestionTurnk = sb.toString();
		System.out.println(allQuestionTurnk);
		QuestionTagBean newQuestionTagBean = new QuestionTagBean();
		//calculate Next Start index
		newQuestionTagBean.setNumber(newStartNumber);
		newQuestionTagBean.setQuestionTrunk(allQuestionTurnk);
		return conversionImgSrcToBase64Src(newQuestionTagBean);
		}else
		{
			return questionTagBean;
		}
	}


	/**imgTag拿到图片的url地址  <img src="http://www.adb.com/dddddd.png"/>"
	 * 拿到http://www.adb.com/...
	 * **/
	public static String getImgTagUrl(String imgUrl){
		if(imgUrl==null){
			return null;
		}
		String[] imgUrlArray = imgUrl.split("\"");
		imgUrl = imgUrlArray[1];
		return imgUrl;
	}


	/**
	 *
	 * @param imgTag
	 * @param newSrc
	 * @return
	 * replace old Tag to be base64
	 */
	public static String replaceImgTagSrcToBase64(String imgTag,String newSrc){
		String[] imgTagArray = imgTag.split("\"");
		String oldSrc = imgTagArray[1];
		imgTag = imgTag.replace(oldSrc, newSrc);
		return imgTag;
	}

	/**
	 * 将imgTag  <img src="data:imag/png base64,saldfasldkjwlkjslkdfjwekrj..."/>转成纯base64
	 * @param imgTag
	 * @return
	 */
	public static String replaceImgTagToBase64(String imgTag) {
		String[] imgArray = imgTag.split(",");
		imgTag = imgArray[1];
		imgTag = imgTag.replace("\"/>", "");
		//System.out.println(imgTag);
		return imgTag;
	}

}
