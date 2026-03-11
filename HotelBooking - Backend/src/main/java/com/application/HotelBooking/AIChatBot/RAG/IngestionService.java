package com.application.HotelBooking.AIChatBot.RAG;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class IngestionService implements CommandLineRunner {

    private JdbcTemplate jdbcTemplate;
    private VectorStore vectorStore;

    private final Logger logger = LoggerFactory.getLogger(IngestionService.class);

    @Value("classpath:docs/hotel-policies.pdf")
    private Resource resource;

    public IngestionService(VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {

            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM vector_store", Integer.class);

            if (count != null && count > 0) {
                logger.info("Vector store already contains data. Skipping ingestion.");
                return;
            }

            logger.info("Starting document ingestion process...");
            var pdfReader = new PagePdfDocumentReader(resource);
            TextSplitter textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(pdfReader.get()));
            logger.info("Vector store loaded with data");
        } catch (Exception e) {
            logger.error("Error during document ingestion: {}", e.getMessage(), e);
        }
    }
}
