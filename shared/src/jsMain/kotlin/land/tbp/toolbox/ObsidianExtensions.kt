// Extension functions for Obsidian API types
package land.tbp.toolbox

import org.w3c.dom.HTMLElement

/**
 * Extension function to add CSS class to HTMLElement
 */
fun HTMLElement.addClass(className: String) {
    this.classList.add(className)
}

/**
 * Extension function to remove CSS class from HTMLElement
 */
fun HTMLElement.removeClass(className: String) {
    this.classList.remove(className)
}

/**
 * Extension function to set text content
 */
fun HTMLElement.setText(text: String) {
    this.textContent = text
}

/**
 * Extension function to empty an element
 */
fun HTMLElement.empty() {
    this.innerHTML = ""
}

