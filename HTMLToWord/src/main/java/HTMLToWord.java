import utils.TagsChecker;
import utils.TestPaperToWordDocx;

public class HTMLToWord{
            public String generateHTMLToWord(String HTMLStr){
                TestPaperToWordDocx testPaperToWordDocx=new TestPaperToWordDocx();

                String allQuestionTrunk=testPaperToWordDocx.createTestPaperTrunk(HTMLStr);
                if(!TagsChecker.check(allQuestionTrunk))//修复html代码
                {
                    allQuestionTrunk=TagsChecker.fix(allQuestionTrunk);
                }
                String fileName=testPaperToWordDocx.createDocumentWord("<!DOCTYPE html [<!ENTITY nbsp \" \">]> <html><body><div><ol>"+allQuestionTrunk+"</ol></div>"+"</body></html>");
                return fileName;
            }
            public static void main(String arg[])
            {
                HTMLToWord htmlToWord=new HTMLToWord();
                htmlToWord.generateHTMLToWord("<div>123145646477777</div>");
            }
        }