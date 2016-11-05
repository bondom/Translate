package ua.translate.model.viewbean;

import org.springframework.format.annotation.NumberFormat;

public class FundsBean {
	
	@NumberFormat
	private double funds= 0;

	public double getFunds() {
		return funds;
	}

	public void setFunds(double funds) {
		this.funds = funds;
	}
}
