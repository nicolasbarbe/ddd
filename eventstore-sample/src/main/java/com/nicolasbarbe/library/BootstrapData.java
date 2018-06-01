package com.nicolasbarbe.library;

import com.nicolasbarbe.library.domain.Library;
import com.nicolasbarbe.library.repository.LibraryRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@Component
public class BootstrapData implements ApplicationRunner {

    private static final Log logger = LogFactory.getLog(BootstrapData.class);

    private LibraryRepository repository;

    public BootstrapData(LibraryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Library library = repository.getLibrary();

        // todo add check to avoid reboostraping data
        Flux.just(
                Tuples.of(
                        "Continuous Delivery: Reliable Software Releases through Build, Test, and Deployment Automation",
                        "0321601912",
                        LocalDate.of(2010, Month.AUGUST, 6)),
                Tuples.of(
                        "The DevOps Handbook: How to Create World-Class Agility, Reliability, and Security in Technology Organizations",
                        "1942788002",
                        LocalDate.of(2016, Month.OCTOBER, 6)),
                Tuples.of(
                        "Site Reliability Engineering: How Google Runs Production Systems",
                        "149192912X",
                        LocalDate.of(2016, Month.APRIL, 16)),
                Tuples.of(
                        "Release It!: Design and Deploy Production-Ready Software",
                        "1680502395",
                        LocalDate.of(2018, Month.JANUARY, 16)))
                .map( ref -> library.addReference(ref.getT1(), ref.getT2(), ref.getT3()) )
                .then( this.repository.save(library, 0) )
                .subscribe( v -> logger.info("Data bootstrapped successfully."), e -> logger.error("Something wrong happened while bootstrapping the data.", e));
    }


}