import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class DoIt {

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Splitter();
	}
}

class Splitter{
	int openP = 0;
	int closeP = 0;
	int qtySplit = 0;
	int tagLevel = 0;
	String tagName = null;
	String fileName = null;
	String xPath = null;
	String[] spl = null;
	
	int count = 0;
	
	boolean string = false;
	boolean flag = false;
	boolean skipData = false;
	int skipLevel = -1;
	int flagLevel = -1;
	
	
	
	BufferedInputStream bin = null;
	
	
	CustomList<String> list;
	String currentTag = "";
	
	// keeps the no. of tag-count in a splitted file
	int tagCounter = 0;
	
	public int level(){
		return this.openP - this.closeP ;
	}
	
	
	public void getParams(){
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter absolute path of the file: ");
		this.fileName = sc.nextLine();
		System.out.println("Enter the xPath: ");
		xPath = sc.nextLine();
		System.out.println("Enter the splitSize: ");
		qtySplit = sc.nextInt();
		sc.close();
		//System.out.println(fileName+ ", " + xPath + ", " + String.valueOf(qtySplit));
	}
	
	public void analyzeTag(){
//		xPath = "/data/item/city";
//		qtySplit = 1000;
//		
		
		String[] spl1 = xPath.split("/");
		spl = new String[spl1.length -1];
		for(int i = 1;i < spl1.length ; i++)
			spl[i-1] = spl1[i];
		tagLevel = spl.length;
		//System.out.println(""+spl[0]);
		//tagName = spl[spl.length - 1];
	}
	
	public Splitter(){
		list = new CustomList<>();
		getParams();
		analyzeTag();
		fetching();
		//System.out.println(""+ openP + ", " + closeP + ", " + (level()) + ", " + count);
	}
	
	char getc(int index, char... content){
		if(index >= content.length){
			try {
				return (char)bin.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return '\0';
			}
		}
		else
			return content[index];
	}
	
	boolean matchTagName(String tag, int index, char... content){
		int ind = index+1;
			while(getc(index++, content) == ' ');
			String name = "";
			char test = getc(index++, content);
			//System.out.println("debug...    "+test+getc(index++, content)+getc(index++, content)+getc(index++, content)+getc(index++, content)+getc(index++, content)+getc(index++, content)+", "+index);
			while(test != '>' && test != ' '){
				name += test;
				test = getc(index++, content);
				//System.out.println("221");
			}
			//System.out.println(","+tag+", ,"+name+",");
			if(tag.compareTo(name) == 0){
				//System.out.println("match....... ");
				return true;
			}
			else
				return false;
	}
	
	// sets the flag
	void openCheck(int index, char... content){
		openP++;
		// check if the tag is the required one
		//System.out.println(""+spl.length);
		if(level() < spl.length){
			String tag = spl[level() -1];
			if(!matchTagName(tag, index, content)){
				skipLevel = level();
				skipData = true;
				//System.out.println("Skip Flag Set");
			}
		}
		else if(level() == spl.length){
			String tag = spl[level() -1];
			if(matchTagName(tag, index, content)){
				flag = true;
				flagLevel = level();
				skipData = false;
				//System.out.println("flag Set");
			}
		}
		else if(!flag){
			skipLevel = level();
			skipData = true;
			//System.out.println("Skip Flag Set");
		}
	}
	
	// unsets the flag
	void closeCheck(int index, char... content){
		// at close check the level and stringdata to get logic for unsetting the flag
		if(skipData){
			if(level() == skipLevel){
				skipData = false;
				skipLevel = -1;
				//System.out.println("Skip Flag Unset");
			}	
		}
		if(flag){
			if(level() == flagLevel){
				flagLevel = -1;
				flag = false;
				list.add(currentTag);
				currentTag = "";
				//System.out.println("Flag Unset");
			}		
		}
		closeP++;
	}
	
	
	public void fetchIt(char... content){
			for (int i=0; i < content.length; i++){
				if(content[i] == '"'){
					//System.out.println("str...");
					if(string)
						string = false;
					else
						string = true;
				}
				if(string)
					continue;
				
				if(content[i] == '<'){
					//System.out.println("open close");
					if(flag)
						currentTag += '<';
					count++;
					if(i+1 >= content.length){
						try {
							char ch = (char)bin.read();
							if(ch == '/'){
								closeCheck(i, content);
							}
							else if(ch == '?'){
								System.out.println("ok");
							}
								
			 				else{
			 					openCheck(i,content);
			 				}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("ouch!!!");
							e.printStackTrace();
						}
					}
					else{
						if(content[i+1] == '/'){
							closeCheck(i, content);
						}
						else if(content[i+1] == '?')
							count--;
	 					else{
	 						openCheck(i, content);
	 					}
					}
				}
				else{
					//System.out.println("oooops!!!");
					if(flag){
						currentTag += content[i];
					}
					else if(skipData)
						continue;
				}
			}
	}
	
	public void fetching(){
		try{
			File fl = new File("C:/Users/Aman.Gaur/Desktop/zips1123.xml");
			bin = new BufferedInputStream(new FileInputStream(fl));
			byte content[] = new byte[1024];
			int bytesRead=0;
            String strFileContents;
            String temp = "";
            System.out.println("started");
            while( (bytesRead = bin.read(content)) != -1){
            	//System.out.println("1 \n");
            	strFileContents = new String(content, 0, bytesRead);
            	//System.out.print(strFileContents);
            	//break;
            	//System.out.println("ok \n");
            	fetchIt(strFileContents.toCharArray());
            	//System.out.println("fft");
            	//temp = strFileContents;
            }
            System.out.println("done");
            for(String ft: list)
            	System.out.println(ft);
//            for(String str: list){
//            	System.out.println(str);
//            	System.out.println();
//            }
		}
		catch (Exception ex){
			System.out.println("Error fetching file");
			System.out.println(ex.getMessage());
		}
	}
	
	
	class CustomList<T> extends ArrayList<T>{
		int count = 0;
		String fileName = "test";
		int fileCounter = 1;
		
		ConnectionFactory factory;
	    Connection connection = factory.newConnection();
	    Channel channel;
		
	    CustomList(){
	    	factory = new ConnectionFactory();
		    factory.setHost("localhost");
		    channel = connection.createChannel();
	    }
	    
		public void writeFile(){
			channel.queueDeclare("temp", false, false, false, null);
		    String message = "";
		    for(String i: list){
		    	message += i;
		    }
		    channel.basicPublish("", "temp", null, message.getBytes());
		    System.out.println(" [x] Sent '" + message + "'");
			// write data to file
			//String fileN = fileName+String.valueOf(fileCounter)+".xml";
			// start temp content
			//System.out.println(super.size());
			super.clear();
			//end
			
			fileCounter++;
		}

		@Override
		public boolean add(T e) {
			// TODO Auto-generated method stub
			if(count >= qtySplit){
				writeFile();
				count=1;
				return super.add(e);
			}
			else{
				count++;
				return super.add(e);
			}
		}
		
	}
}
