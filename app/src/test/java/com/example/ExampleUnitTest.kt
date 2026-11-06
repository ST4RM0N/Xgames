package com.example

import com.example.parser.WorkflowParser
import com.example.parser.WorkflowTemplates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWorkflowParserDecomposition() {
        val template = WorkflowTemplates.templates.first()
        val result = WorkflowParser.parse(template.sampleDocument)

        assertNotNull(result)
        assertTrue(result.title.isNotEmpty())
        assertTrue("Expected parsed steps", result.steps.isNotEmpty())

        val firstStep = result.steps.first()
        assertEquals("TRIGGER", firstStep.category)
        assertTrue(firstStep.botPayload.contains("EVENT_LISTENER"))
    }
}
