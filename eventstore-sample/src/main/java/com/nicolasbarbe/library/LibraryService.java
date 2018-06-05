package com.nicolasbarbe.library;

import com.nicolasbarbe.ddd.eventstore.EventStore;
import com.nicolasbarbe.ddd.eventstore.http.HttpClientEventStore;
import com.nicolasbarbe.library.command.BorrowBookCommand;
import com.nicolasbarbe.library.command.BorrowBookCommandHandler;
import com.nicolasbarbe.library.command.ReturnBookCommand;
import com.nicolasbarbe.library.command.ReturnBookCommandHandler;
import com.nicolasbarbe.library.event.BookReferenceAdded;
import com.nicolasbarbe.library.event.NewLibraryCreated;
import com.nicolasbarbe.library.projection.ListOfBooks;
import com.nicolasbarbe.library.repository.LibraryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@SpringBootApplication
public class LibraryService {

	private ListOfBooks listOfBooks;

	public LibraryService() {
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryService.class, args);
	}

	@Bean
	protected BorrowBookCommandHandler getBorrowBookCommandHandler(LibraryRepository repository) {
		return new BorrowBookCommandHandler(repository);
	}

	@Bean
	protected ReturnBookCommandHandler getReturnBookCommandHandler(LibraryRepository repository) {
		return new ReturnBookCommandHandler(repository);
	}

	@Bean
	protected EventStore getEventStore() {
		return new HttpClientEventStore("http://localhost:8080");
	}


	@Bean
	protected RouterFunction<ServerResponse> routes(BorrowBookCommandHandler borrowBookCommandHandler, ReturnBookCommandHandler returnBookCommandHandler) {
		return RouterFunctions
				.route( RequestPredicates.POST("/commands/borrowBook")
						.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
						request -> borrowBookCommandHandler(request, borrowBookCommandHandler))

				.andRoute( RequestPredicates.POST("/commands/returnBook")
						.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
						request -> returnBookCommandHandler(request, returnBookCommandHandler))

				.andRoute( RequestPredicates.GET("/library"),  this::listBooks);
	}



	private Mono<ServerResponse> borrowBookCommandHandler(ServerRequest request, BorrowBookCommandHandler borrowBookCommandHandler) {
		return borrowBookCommandHandler.handle(request.bodyToMono(BorrowBookCommand.class))
				.then( ok().build() );
	}

	private Mono<ServerResponse> returnBookCommandHandler(ServerRequest request, ReturnBookCommandHandler returnBookCommandHandler) {
		return returnBookCommandHandler.handle(request.bodyToMono(ReturnBookCommand.class))
				.then( ok().build() );
	}

	private Mono<ServerResponse> listBooks(ServerRequest request) {
		return this.getEventStore().listEventStreams()
				// we assume there is only one event stream
				.take(1)
				.flatMap( stream -> this.getEventStore().allEvents(stream.getEventStreamId()))
				.log()
				.reduce( new ListOfBooks(), (books, event) -> {
					Object payload = event.getData();
					 if ( payload instanceof  BookReferenceAdded) {
						 books.getBooks().add(new ListOfBooks.Book(((BookReferenceAdded) payload).getTitle()));
					 } else if(payload instanceof  NewLibraryCreated) {
						 // do nothing
					 }
					 return books;
				})
				.flatMap( books -> ok().body( fromObject(books) ));
	}

}
