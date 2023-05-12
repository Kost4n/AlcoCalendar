package com.kost4n.alcocalendar

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.tibolte.agendacalendarview.render.EventRenderer

class DrawableEventRenderer : EventRenderer<DrawableCalendarEvent>() {
    // region Class - EventRenderer
    override fun render(view: View, event: DrawableCalendarEvent) {
        val imageView: ImageView = view.findViewById(R.id.view_agenda_event_image) as ImageView
        val txtTitle: TextView =
            view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_title) as TextView
        val txtLocation: TextView =
            view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_location) as TextView
        val descriptionContainer: LinearLayout =
            view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_description_container) as LinearLayout
        val locationContainer: LinearLayout =
            view.findViewById(com.github.tibolte.agendacalendarview.R.id.view_agenda_event_location_container) as LinearLayout
        descriptionContainer.setVisibility(View.VISIBLE)
        imageView.setImageDrawable(view.getContext().getResources().getDrawable(event.drawableId))
        txtTitle.setTextColor(view.getResources().getColor(android.R.color.black))
        txtTitle.setText(event.title)
        txtLocation.setText(event.location)
        if (event.location.length > 0) {
            locationContainer.setVisibility(View.VISIBLE)
            txtLocation.setText(event.location)
        } else {
            locationContainer.setVisibility(View.GONE)
        }
        if (event.title == view.getResources()
                .getString(com.github.tibolte.agendacalendarview.R.string.agenda_event_no_events)
        ) {
            txtTitle.setTextColor(view.getResources().getColor(android.R.color.black))
        } else {
            txtTitle.setTextColor(
                view.getResources().getColor(com.github.tibolte.agendacalendarview.R.color.theme_text_icons)
            )
        }
        descriptionContainer.setBackgroundColor(event.color)
        txtLocation.setTextColor(
            view.getResources().getColor(com.github.tibolte.agendacalendarview.R.color.theme_text_icons)
        )
    }

    override fun getEventLayout(): Int {
        return R.layout.view_agenda_drawable_event
    }

    override fun getRenderType(): Class<DrawableCalendarEvent> {
        return DrawableCalendarEvent::class.java
    } // endregion
}