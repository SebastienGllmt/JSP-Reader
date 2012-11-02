package reader;

import java.util.ArrayList;
import java.util.List;

public class JSPScanner {

	private int[] content;
	private int count=0;
	
	private int length=0;
	private int position=0;
	private int offset=0;
	private List<Integer> head = new ArrayList<Integer>();
	
	public JSPScanner(int[] info){
		content = info;
	}
	
	
	public boolean hasNextByte(){
		if(!hasNext()){
			return false;
		}
		if(length == 0){
			position = 0;
			return false;
		}else{
			if(length-position > 0){
				return true;
			}else{
				length = 0;
				position = 0;
				return false;
			}
		}
	}
	public int nextByte(){
		int value = next();
		position++;
		return value;
	}
	
	public boolean hasNext(){
		if(count < content.length){
			return true;
		}else{
			return false;
		}
	}
	public int next(){
		int value = content[count];
		count++;
		return value;
	}
	public int peek(){
		return content[count];
	}
	
	public void skipHead(){
		position=0;
		head.clear();
		if(!hasNext()){
			return;
		}
		int value;
		do{
			value = next();
			head.add(value);
			offset--;
			offset += value-128;
		}while(hasNext() && value > 128);
		offset -= value-128;
		length = value;
		head.remove(head.size()-1);
	}
	
	public int getIndex(){
		return count-1;
	}
	public void setIndex(int index){
		count = index;
	}
	/**
	 * Gets the position including the offset from blank bytes
	 * Warning: Will only work with byteStream()
	 * @return - the position in an image
	 */
	public int getRealPosition(){
		return getIndex()+offset;
	}
	public int getLength(){
		return length;
	}
	public void setLength(int value){
		length = value;
	}
	public List<Integer> getHead(){
		return head;
	}
	
	public int byteStream(){
		if(!hasNextByte()){
			skipHead();
		}
		int value;
		if(hasNext()){
			value = nextByte();
		}else{
			value = -1;
		}
		return value;
	}
}
