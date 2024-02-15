package product;

import java.util.Date;

public class Cashflow {
	
	private Date date;
	private int productId;
	private String type;
	private String direction;
	
	public Date date() {
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

	

}
