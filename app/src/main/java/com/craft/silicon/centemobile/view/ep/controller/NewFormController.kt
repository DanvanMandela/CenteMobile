package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.ControlList
import com.craft.silicon.centemobile.view.ep.data.GroupForm

class NewFormController(val callbacks: AppCallbacks) :
    TypedEpoxyController<GroupForm>() {
    override fun buildModels(data: GroupForm?) {
        data?.let {
            val sDat = it.form
            val remove = arrayListOf<Int>()
            val finalList = mutableListOf<ControlList>()
            sDat?.forEachIndexed { index, formControl ->
                if (BaseClass.nonCaps(formControl.controlID) == BaseClass.nonCaps(ControlTypeEnum.CONTAINER.type)) {
                    val items = mutableListOf<ControlList>()
                    sDat.forEachIndexed { indexTwo, formTwo ->
                        if (BaseClass.nonCaps(formTwo.containerID) == BaseClass.nonCaps(formControl.containerID)) {
                            val itemsTwo = ControlList(
                                formControl = formTwo,
                                mutableListOf(),
                                container = false,
                                linked = true
                            )
                            items.add(itemsTwo)
                            remove.add(indexTwo)
                        }
                    }
                    remove.add(index)

                    val itemsTwo = ControlList(
                        formControl = formControl,
                        items,
                        container = true,
                        linked = false
                    )
                    finalList.add(itemsTwo)
                }
            }

            remove.sort()

            var removedCount = 0

            remove.forEach { i ->
                sDat?.removeAt(i.minus(removedCount))
                removedCount.inc()
            }
            remove.clear()
            removedCount = 0

            val linked = mutableListOf<ControlList>()

            finalList.forEach { fl ->
                fl.list.forEach { il ->
                    sDat?.forEachIndexed { indexTwo, formTwo ->
                        if (BaseClass.nonCaps(il.formControl.controlID) == BaseClass.nonCaps(formTwo.linkedToControl)) {
                            val itemsTwo = ControlList(
                                formControl = formTwo,
                                mutableListOf(),
                                container = false,
                                linked = true
                            )
                            linked.add(itemsTwo)
                            remove.add(indexTwo)
                        }
                    }
                }
            }

            remove.forEach { i ->
                sDat?.removeAt(i.minus(removedCount))
                removedCount.inc()
            }

            finalList.addAll(linked)

            sDat?.forEach { d ->
                val itemsTwo = ControlList(
                    formControl = d,
                    mutableListOf(),
                    container = false,
                    linked = false
                )
                finalList.add(itemsTwo)
            }
            sDat?.clear()

            finalList.forEach { dh ->

            }
        }

    }
}