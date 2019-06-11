package com.dev.browser.concept.searchbar

interface SearchBar{
    /**
     * Registers the given listener to be invoked when the user edits the URL.
     */
    fun setOnEditListener(listener: OnEditListener?)
    /**
     * Listener to be invoked when the user edits the URL.
     */
    interface OnEditListener {
        /**
         * Fired when the toolbar switches to edit mode.
         */
        fun onStartEditing() = Unit

        /**
         * Fired when the toolbar switches back to display mode.
         */
        fun onStopEditing() = Unit

        /**
         * Fired whenever the user changes the text in the address bar.
         */
        fun onTextChanged(text: String) = Unit
    }

}