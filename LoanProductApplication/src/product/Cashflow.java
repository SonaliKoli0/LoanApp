package product;

import java.util.Date;

import utils.Util;

public class Cashflow {
	
	private Date date;
	private int productId;
	private String type;
	private String direction;
	
	public Cashflow(int id, Date date, String type, String direction) {
		this.productId=id;
		this.date=date;
		this.type=type;
		this.direction=direction;
	}
	public Cashflow(){
		
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
    
	public String toString(){
		return productId+"\t"+Util.formatDate(this.getDate())+"\t"+this.getDirection()+"\t"+getType()+"\t";
	}

}
