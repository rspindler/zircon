package org.hexworks.zircon.internal.fragment.impl

import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.collection.ListProperty
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.behavior.TextHolder
import org.hexworks.zircon.api.component.HBox
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.fragment.Selector
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.uievent.ComponentEventType

class DefaultSelector<T : Any>(
        parent: HBox,
        defaultSelected: T,
        initialValues: Iterable<T>,
        private val centeredText: Boolean = true,
        private val toStringMethod: (T) -> String = Any::toString,
        clickable: Boolean = false
) : Selector<T> {

    override val valuesProperty = initialValues.toProperty()
    override val values: List<T> by valuesProperty.asDelegate()

    private val indexProperty = createPropertyFrom(values.indexOf(defaultSelected))

    override val selectedValue: ObservableValue<T> = indexProperty.bindTransform {
        values[it]
    }
    override val selected: T
        get() = selectedValue.value

    private val rightButton = Components.button().withText(Symbols.ARROW_RIGHT.toString()).withDecorations().build().apply {
        processComponentEvents(ComponentEventType.ACTIVATED) { showNextValue() }
    }

    private val leftButton = Components.button().withText(Symbols.ARROW_LEFT.toString()).withDecorations().build().apply {
        processComponentEvents(ComponentEventType.ACTIVATED) { showPrevValue() }
    }

    private val labelSize = Size.create(parent.contentSize.width - (leftButton.width + rightButton.width), 1)

    override val root = parent.apply {
        addComponent(leftButton)

        if (clickable) {
            addComponent(Components.button().withDecorations().withSize(labelSize).build().apply {
                initLabel()
                processComponentEvents(ComponentEventType.ACTIVATED) {
                    showNextValue()
                }
            })
        } else {
            addComponent(Components.label()
                    .withSize(labelSize)
                    .build().apply {
                        initLabel()
                    })
        }

        addComponent(rightButton)
    }

    private fun TextHolder.initLabel() {
        text = fetchLabelBy(0)
        textProperty.updateFrom(indexProperty) { i -> fetchLabelBy(i) }
    }

    private fun setValue(from: Int, to: Int) {
        indexProperty.value = to
    }

    private fun showNextValue() {
        val oldIndex = indexProperty.value
        var nextIndex = oldIndex + 1
        if (nextIndex >= values.size) {
            nextIndex = 0
        }
        setValue(oldIndex, nextIndex)
    }

    private fun showPrevValue() {
        val oldIndex = indexProperty.value
        var prevIndex = oldIndex - 1
        if (prevIndex < 0) {
            prevIndex = values.size - 1
        }
        setValue(oldIndex, prevIndex)
    }

    private fun fetchLabelBy(index: Int) = toStringMethod.invoke(values[index]).centered()

    private fun String.centered(): String {
        val maxWidth = labelSize.width
        return if (centeredText && length < maxWidth) {
            val spacesCount = (maxWidth - length) / 2
            this.padStart(spacesCount + length).padEnd(maxWidth)
        } else {
            this.substring(0, kotlin.math.min(length, maxWidth))
        }
    }

}
