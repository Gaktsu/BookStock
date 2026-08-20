package com.booksefter.book_ledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookLedgerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookLedgerApplication.class, args);
	}

}
