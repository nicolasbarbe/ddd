package com.nicolasbarbe.library;

import com.nicolasbarbe.ddd.eventstore.EventStore;
import com.nicolasbarbe.ddd.eventstore.http.HttpClientEventStore;
import com.nicolasbarbe.library.event.BookCopyBorrowed;
import com.nicolasbarbe.library.event.BookReferenceAdded;
import com.nicolasbarbe.library.event.NewLibraryCreated;
import com.nicolasbarbe.library.projection.MostSuccessfullBooks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@SpringBootApplication
public class LibraryQueryService {

	private MostSuccessfullBooks listOfBooks;

	public LibraryQueryService() {
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryQueryService.class, args);
	}

	@Bean
	protected EventStore getEventStore() {
		return new HttpClientEventStore("http://localhost:8080");
	}

	@Bean
	protected RouterFunction<ServerResponse> routes() {
		return RouterFunctions
				.route(  RequestPredicates.GET("/library"),  this::listBooks);
	}


	private Mono<ServerResponse> listBooks(ServerRequest request) {
		return this.getEventStore().listEventStreams()
				// we assume there is only one event stream
				.take(1)
				.flatMap( stream -> this.getEventStore().allEvents(stream.getEventStreamId()))
				.log()
				.reduce( new MostSuccessfullBooks(), (books, event) -> {
					Object payload = event.getData();
					 if ( payload instanceof BookReferenceAdded) {
						 BookReferenceAdded e = (BookReferenceAdded) payload;
						 books.getBooks().put( e.getIsbn(), new MostSuccessfullBooks.Book(e.getTitle()));
					 } else if(payload instanceof NewLibraryCreated) {
						 // do nothing
					 } else if(payload instanceof BookCopyBorrowed) {
					 	books.getBooks().get(((BookCopyBorrowed) payload).getIsbn()).increment();
					 }
					 return books;
				})
				.flatMap( books -> ok().body( fromObject(books.getTop10()) ));
	}

}
