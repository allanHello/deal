package com.data.deal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class App 
{
    public static void main( String[] args )
    {
    	SXSSFWorkbook workbook = new SXSSFWorkbook();
    	Sheet sheet = workbook.createSheet();
    	int rowindex = 0;
    	int error = 0;
    	boolean flag = false;  //超长flag
    	final int celllimit = 32760; //每个单元格的极限长度，比实际的32768要小
    	Row rowHeader = sheet.createRow(rowindex);
    	String[] headerTitle = {"序号", "股票代码", "日期", "简称", "公告名", "公告内容"};
    	int[] widths = {6, 10, 12, 12, 50, 150};
    	for (int i = 0; i < headerTitle.length; i++) {
    		sheet.setColumnWidth(i, widths[i] * 256);
    	}
    	CellStyle style = getHeaderStyle(workbook);
    	for (int i = 0; i < headerTitle.length; i++) {
    		Cell cell = rowHeader.createCell(i);
    		cell.setCellValue(headerTitle[i]);
    		cell.setCellStyle(style);
    	}
    	rowindex++;
    	
        File file = new File("E:\\project\\java\\data");
        File[] files = file.listFiles();
        
        for (File file1 : files) {
        	File[] files1 = file1.listFiles();
        	System.out.println(files1.length);
        	for (File file2 : files1) {
        		String filepath = file2.getAbsolutePath();
        		String filename = file2.getName();
        		System.out.println(filename);
        		
        		String shortAndtitle = filename.substring(21, filename.length() - 4);
        		
        		//正常以下划线分开的
				String[] splits = shortAndtitle.split("_");
				if (splits.length < 2) {
					//以中文'.'分割的有一些文件
					splits = shortAndtitle.split("：");
					if (splits.length < 2) {
						splits = new String[2];
						
						//以股票代码开头的，比较乱
						Pattern pattern = Pattern.compile("(^\\d{6})([_\\s：]*)(.*)");
						Matcher match = pattern.matcher(shortAndtitle);
						boolean find = match.find();
						if (find) {
							splits[0] = match.group(1);
							splits[1] = match.group(3);
						} else {  //分不开的特殊处理						
							splits[0] = "";
							splits[1] = shortAndtitle;
						}
					}
				}
        		
				String content = "";
				try {
					content = getTextFromPdf(filepath);
				} catch(Exception ex) {
				}
        		
        		
        		//文档内容太多的，过滤
        		if (content == null || content.length() == 0) {
        			error++;
        			continue;
        		}
        		
        		if (content.length() > celllimit) {
        			flag = true;
        		}
        		
        		Row rowContent = sheet.createRow(rowindex);
        		Cell cellNo = rowContent.createCell(0);
        		cellNo.setCellValue(rowindex);
        		
        		Cell stockCode = rowContent.createCell(1); 
        		stockCode.setCellValue(filename.substring(0, 6));
        		
        		Cell cellDate = rowContent.createCell(2);
        		String strDate = filename.substring(10, 20);
        		CellStyle dateStyle = workbook.createCellStyle();
        		dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd"));
        		cellDate.setCellStyle(dateStyle);
				cellDate.setCellValue(strDate);
				
				Cell cellShorter = rowContent.createCell(3);
        		cellShorter.setCellValue(splits[0]);
        		Cell cellTitle = rowContent.createCell(4);
        		cellTitle.setCellValue(splits[1]);
        		
        		if (flag) {  //超长的单元格处理
        			int length = content.length();
        			int addcellIndex = 5;
        			int beginIndex = 0;
        			
        			while (beginIndex < length) {
        				if (beginIndex + celllimit <= length) {
        					Cell cellContent = rowContent.createCell(addcellIndex++);
        					cellContent.setCellValue(content.substring(beginIndex, beginIndex + celllimit));
        					beginIndex += celllimit;
        				}  else {
        					Cell cellContent = rowContent.createCell(addcellIndex++);
        					cellContent.setCellValue(content.substring(beginIndex, length));
        					break;
        				}
        			}
        			
        		} else {
        			Cell cellContent = rowContent.createCell(5);
            		cellContent.setCellValue(content);
        		}
        		
        		rowindex++;       		
        	}
        }
        
        File outfile = new File("E:\\project\\java\\数据.xlsx");
        OutputStream fos = null;
        try {
			fos = new FileOutputStream(outfile);
			workbook.write(fos);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("success!");
        System.out.println(String.format("异常数量:%d", error));
        
    }
    
    private static String getTextFromPdf(String filepath) {
    	String result = null;
    	FileInputStream is = null;
    	PDDocument document = null;
    	
    	try {
			is = new FileInputStream(filepath);
			PDFParser parser = new PDFParser(is);
			parser.parse();
			document = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			result = stripper.getText(document);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	
    	
    	return result;
    }
    
    private static CellStyle getHeaderStyle(Workbook workbook) {
    	CellStyle style = workbook.createCellStyle();
    	style.setAlignment(HorizontalAlignment.CENTER);
    	Font font = workbook.createFont();
    	font.setBold(true);
    	style.setFont(font);
    	return style;
    }
}
