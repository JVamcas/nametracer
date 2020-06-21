package com.petruskambala.namcovidcontacttracer.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.petruskambala.namcovidcontacttracer.ui.person.PersonViewModel

abstract class AbstractFragment : Fragment() {
    val personModel: PersonViewModel by activityViewModels()
}