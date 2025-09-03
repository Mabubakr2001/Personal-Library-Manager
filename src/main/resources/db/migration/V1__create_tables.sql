CREATE TABLE authors (
  author_id bigint NOT NULL AUTO_INCREMENT,
  full_name varchar(50) NOT NULL,
  PRIMARY KEY (author_id)
);

CREATE TABLE categories (
  category_id bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (category_id)
);

CREATE TABLE publishers (
  publisher_id bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (publisher_id)
);

CREATE TABLE readers (
  reader_id bigint NOT NULL AUTO_INCREMENT,
  email varchar(255) NOT NULL,
  is_enabled boolean NOT NULL,
  password varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  verification_code varchar(6) DEFAULT NULL,
  verification_expiration datetime(6) DEFAULT NULL,
  PRIMARY KEY (reader_id)
);

CREATE TABLE books (
  book_id bigint NOT NULL AUTO_INCREMENT,
  title varchar(255) NOT NULL,
  subtitle varchar(500) DEFAULT NULL,
  description text DEFAULT NULL,
  isbn varchar(13) NOT NULL,
  image_link varchar(2000) DEFAULT NULL,
  pages_count int NOT NULL,
  printing_type varchar(255) DEFAULT NULL,
  publishing_year int DEFAULT NULL,
  author_id bigint DEFAULT NULL,
  category_id bigint DEFAULT NULL,
  publisher_id bigint DEFAULT NULL,
  PRIMARY KEY (book_id),
  CONSTRAINT fk_book_publisher FOREIGN KEY (publisher_id) REFERENCES publishers (publisher_id),
  CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES authors (author_id),
  CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES categories (category_id)
);

CREATE TABLE readers_books (
  adding_date date DEFAULT NULL,
  left_off_page int DEFAULT NULL,
  status varchar(255) NOT NULL,
  book_id bigint NOT NULL,
  reader_id bigint NOT NULL,
  PRIMARY KEY (book_id,reader_id),
  CONSTRAINT fk_book_reader FOREIGN KEY (reader_id) REFERENCES readers (reader_id),
  CONSTRAINT fk_reader_book FOREIGN KEY (book_id) REFERENCES books (book_id)
);

CREATE TABLE quotes (
  quote_id bigint NOT NULL AUTO_INCREMENT,
  page_number int NOT NULL,
  text TEXT NOT NULL,
  book_id bigint DEFAULT NULL,
  reader_id bigint DEFAULT NULL,
  PRIMARY KEY (quote_id),
  CONSTRAINT fk_reader_book_quote FOREIGN KEY (book_id, reader_id) REFERENCES readers_books (book_id, reader_id)
);

CREATE TABLE words (
  word_id bigint NOT NULL AUTO_INCREMENT,
  page_number int NOT NULL,
  related_sentence varchar(2000) NOT NULL,
  translation varchar(255) NOT NULL,
  word_content varchar(45) NOT NULL,
  book_id bigint DEFAULT NULL,
  reader_id bigint DEFAULT NULL,
  PRIMARY KEY (word_id),
  CONSTRAINT fk_reader_book_word FOREIGN KEY (book_id, reader_id) REFERENCES readers_books (book_id, reader_id)
);