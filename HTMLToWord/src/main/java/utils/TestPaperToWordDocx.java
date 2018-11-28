package utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;

import Beans.QuestionTagBean;

import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Document;

import fmath.conversion.ConvertFromLatexToMathML;


public class TestPaperToWordDocx {

	/** 将latex转为mathml  $公式$ **/
	public  String latexToOOXML(String inStr) {
		inStr=inStr.replace(" ", "");
		String mathml = ConvertFromLatexToMathML.convertToMathML(inStr);
		String ooxmlMath = fmath.conversion.c.a.a(mathml);
		return ooxmlMath;
	}
	
	
	/**生成一道试题的ooxml**/
	public  String createDocumentWord(String allQuestionTrunk) {
		String ooxmlAllQuestionTrunk = null;
		String filePath = "";//the file path for saving the word doc
		String uploadName =  "./test1.docx";
		try {
			QuestionTagBean questionTagBean = new QuestionTagBean();
			questionTagBean.setNumber(0);
			questionTagBean.setQuestionTrunk(allQuestionTrunk);
			QuestionTagBean questionTagBeanImgBase64 = QuestionsImgUtil.conversionImgSrcToBase64Src(questionTagBean);//transfer img to be base64
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
			XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
			allQuestionTrunk = questionTagBeanImgBase64.getQuestionTrunk();
			
			wordMLPackage.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(allQuestionTrunk, null));
			ooxmlAllQuestionTrunk = XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true);
			questionTagBean = new QuestionTagBean();
			questionTagBean.setNumber(0);
			questionTagBean.setQuestionTrunk(ooxmlAllQuestionTrunk);
			ooxmlAllQuestionTrunk = conversionStrMathToOOXMLMath(questionTagBean);
			Document d =  (Document) XmlUtils.unmarshalString(ooxmlAllQuestionTrunk);
			wordMLPackage.getMainDocumentPart().setJaxbElement(d);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			wordMLPackage.save(baos);
			FileUtils.byteToFile(baos.toByteArray(),filePath+uploadName);// you can save binary data to anywhere you what
			//wordMLPackage.save(new java.io.File("E:/educate/upload/OUT_from_XHTML.docx")); the quicker way
		} catch (Exception e) {
			e.printStackTrace();
			uploadName="0";
		}
		return uploadName;
	}
	
	/**
	 * 题干已经转为ooxml，这个方法是将ooxml中的公式转为ooxmlMath
	 * 将提干中的string类型公式转为OOXML公式**/
	public  String conversionStrMathToOOXMLMath(QuestionTagBean questionTagBean){
		String questionTrunk = questionTagBean.getQuestionTrunk();
		int number = questionTagBean.getNumber();
		int startMathNumber = questionTrunk.indexOf("\\(", number);
		if(startMathNumber<0){
			return questionTagBean.getQuestionTrunk();
		}
		int endMathNumber = questionTrunk.indexOf("\\)", startMathNumber+2);
		int wordTextTagNumber = questionTrunk.indexOf("</w:t>", endMathNumber);
		int wordRTagNumber = questionTrunk.indexOf("</w:r>", endMathNumber);
		String otherText = questionTrunk.substring(endMathNumber+2, wordTextTagNumber);//获取到该公式之后的text部分
		if(otherText!=null){
			otherText = "<w:r><w:t>"+otherText+"</w:t></w:r>";
		}
		String math = questionTrunk.substring(startMathNumber, endMathNumber+2);//拿到公式
		String ooxmlMath = latexToOOXML(math);//将公式转微ooxmlMath
		StringBuffer sb = new StringBuffer();
		sb.append(questionTrunk);
		String addToString = ooxmlMath+otherText;
		sb.insert(wordRTagNumber+6,addToString);//将转换后的公式和公式后面的文字放入到试题后面
		sb.delete(startMathNumber, wordTextTagNumber);//删除掉之前的公式和公式后的文字
		int fromIndex = sb.indexOf(addToString, startMathNumber);
		//fromIndex = fromIndex+addToString.length();//下一次替换的位置
		questionTagBean.setNumber(fromIndex);
		questionTagBean.setQuestionTrunk(sb.toString());
		TestPaperToWordDocx testPaperToWordDocx=new TestPaperToWordDocx();
		return testPaperToWordDocx.conversionStrMathToOOXMLMath(questionTagBean);
	}

	
	/**包装试题题干**/
	public  String createTestPaperTrunk(String strQuestionTrunk){
		strQuestionTrunk = "<div><p><li class=\"ac a DocDefaults \" style=\"position: relative; margin-left: -8mm;\">"+strQuestionTrunk+"</li></p><p></p></div>";
		return strQuestionTrunk;
	}


}
