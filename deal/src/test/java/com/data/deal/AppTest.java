package com.data.deal;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;


/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void testApp()
    {
        assertTrue( true );
    }
    
    @Test
    public void testReg() {
    	Pattern p = Pattern.compile("(^\\d{6})([_\\s��]*)(.*)");
    	String s0 = "601519ST�ǻ��ش�����ͣ�ƹ���";
    	String s1 = "601555 _ ����֤ȯ�ش�����ͣ�ƹ���";
    	String s2 = "[CDATA[601588 ����ʵҵ���ڳﻮ�ǹ������й�Ʊ�������ͣ�ƹ���]]";
    	String s3 = "601588 ����ʵҵ��Ʊ������ʾ�Թ���";
    	String s4 = "601599_ ¹�ۿƼ��ش�����ͣ�ƹ���";
    	String s5 = "�չ��_�ش��ʲ����鼰����ͣ�ƹ���";
    	
    	Matcher m0 = p.matcher(s0);
        boolean f0 = m0.find();
        String fr0_1 = m0.group(1);
        String fr0_2 = m0.group(3);
        
        Matcher m1 = p.matcher(s1);
        boolean f1 = m1.find();
        String fr1_1 = m1.group(1);
        String fr1_2 = m1.group(3);
        
        Matcher m2 = p.matcher(s2);
        boolean f2 = m2.find();
        
        Matcher m3 = p.matcher(s3);
        boolean f3 = m3.find();
        String fr3_1 = m3.group(1);
        String fr3_2 = m3.group(3);
        
        Matcher m4 = p.matcher(s4);
        boolean f4 = m4.find();
        String fr4_1 = m4.group(1);
        String fr4_2 = m4.group(3);
        
        Matcher m5 = p.matcher(s5);
        boolean f5 = m5.find();
        
        assertTrue(f0);
    }
}
