package com.nicolasbarbe.library;


import com.nicolasbarbe.ddd.eventstore.http.HttpClientEventStoreConfiguration;
import com.nicolasbarbe.library.command.BorrowBookCommand;
import com.nicolasbarbe.library.command.BorrowBookCommandHandler;
import com.nicolasbarbe.library.command.ReturnBookCommand;
import com.nicolasbarbe.library.command.ReturnBookCommandHandler;
import com.nicolasbarbe.library.repository.LibraryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@SpringBootApplication
@Import( {
		HttpClientEventStoreConfiguration.class,
} )
public class LibraryCommandService {
	
	public LibraryCommandService() {
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryCommandService.class, args);
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
	protected RouterFunction<ServerResponse> routes(BorrowBookCommandHandler borrowBookCommandHandler, ReturnBookCommandHandler returnBookCommandHandler) {
		return RouterFunctions
				.route( RequestPredicates.POST("/commands/borrowBook")
						.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
						request -> borrowBookCommandHandler(request, borrowBookCommandHandler))

				.andRoute( RequestPredicates.POST("/commands/returnBook")
						.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
						request -> returnBookCommandHandler(request, returnBookCommandHandler));
	}



	private Mono<ServerResponse> borrowBookCommandHandler(ServerRequest request, BorrowBookCommandHandler borrowBookCommandHandler) {
		return borrowBookCommandHandler.handle(
				request.bodyToMono(BorrowBookCommand.class)
						.switchIfEmpty( Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid command."))))
				.then( ok().build() );
	}

	private Mono<ServerResponse> returnBookCommandHandler(ServerRequest request, ReturnBookCommandHandler returnBookCommandHandler) {
		return returnBookCommandHandler.handle(
				request.bodyToMono(ReturnBookCommand.class)
						.switchIfEmpty( Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid command."))))
				.then( ok().build() );
	}


}
