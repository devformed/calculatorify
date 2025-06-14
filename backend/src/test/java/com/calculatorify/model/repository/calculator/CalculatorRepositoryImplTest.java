package com.calculatorify.model.repository.calculator;

import com.calculatorify.model.dto.calculator.CalculatorDto;
import com.calculatorify.model.dto.calculator.CalculatorEntry;
import com.calculatorify.model.dto.calculator.config.CalculatorConfig;
import com.calculatorify.model.repository.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class CalculatorRepositoryImplTest extends AbstractTest {

    private final CalculatorRepository repo = new CalculatorRepositoryImpl();

    @BeforeEach
    public void preconditions() {
        List<CalculatorEntry> random = repo.findAllByFullText("nonexistent");
        assertTrue(random.isEmpty());
    }

    @Test
    public void testFindNotFound() {
        List<CalculatorEntry> result = repo.findAllByFullText("none");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testPersistAndFind() {
        Instant now = Instant.now();
        CalculatorConfig config = new CalculatorConfig();
        CalculatorDto dto = CalculatorDto.builder()
            .id(TEST_UUID)
            .title("MyCalc")
            .description("A test calculator")
            .config(config)
            .createdAt(now)
            .updatedAt(now)
            .userId(TEST_UUID)
            .build();
        UUID id = repo.persist(dto);
        assertNotNull(id);

        List<CalculatorEntry> entries = repo.findAllByFullText("MyCalc");
        assertFalse(entries.isEmpty());
        assertEquals(1, entries.size());
        CalculatorEntry entry = entries.get(0);
        assertEquals(id, entry.getId());
        assertEquals("MyCalc", entry.getTitle());
        assertEquals("A test calculator", entry.getDescription());
        assertEquals(TEST_UUID, entry.getUserId());
        assertEquals(now, entry.getCreatedAt());
        assertEquals(now, entry.getUpdatedAt());
        assertNotNull(entry.getConfig());
    }

    @Test
    public void testMerge() {
        Instant now = Instant.now();
        CalculatorConfig config = new CalculatorConfig();
        CalculatorDto dto = CalculatorDto.builder()
            .id(TEST_UUID)
            .title("Calc1")
            .description("Desc1")
            .config(config)
            .createdAt(now)
            .updatedAt(now)
            .userId(TEST_UUID)
            .build();
        UUID id = repo.persist(dto);

        List<CalculatorEntry> entries1 = repo.findAllByFullText("Calc1");
        assertFalse(entries1.isEmpty());
        assertEquals(1, entries1.size());
        CalculatorEntry entry = entries1.get(0);
        entry.setTitle("Calc2");
        entry.setDescription("Desc2");
        entry.setUpdatedAt(now.plusSeconds(60));
        repo.merge(entry);

        List<CalculatorEntry> updatedList = repo.findAllByFullText("Calc2");
        assertFalse(updatedList.isEmpty());
        assertEquals(1, updatedList.size());
        CalculatorEntry updated = updatedList.get(0);
        assertEquals(id, updated.getId());
        assertEquals("Calc2", updated.getTitle());
        assertEquals("Desc2", updated.getDescription());
        assertEquals(entry.getUpdatedAt(), updated.getUpdatedAt());
    }
}