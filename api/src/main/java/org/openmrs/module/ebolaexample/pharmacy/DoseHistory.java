package org.openmrs.module.ebolaexample.pharmacy;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoseHistory {

    private List<DrugOrder> orders;
    private List<ScheduledDose> doses;

    public DoseHistory(List<DrugOrder> orders, List<ScheduledDose> doses) {
        this.orders = orders;
        this.doses = doses;
        if (this.orders != null) {
            Collections.sort(this.orders, new Comparator<DrugOrder>() {
                @Override
                public int compare(DrugOrder left, DrugOrder right) {
                    return OpenmrsUtil.compareWithNullAsEarliest(right.getEffectiveStartDate(), left.getEffectiveStartDate());
                }
            });
        }
        if (this.doses != null) {
            Collections.sort(this.doses, new Comparator<ScheduledDose>() {
                @Override
                public int compare(ScheduledDose left, ScheduledDose right) {
                    return OpenmrsUtil.compareWithNullAsEarliest(right.getScheduledDatetime(), left.getScheduledDatetime());
                }
            });
        }
    }

    public List<DrugOrder> getOrders() {
        return orders;
    }

    public List<ScheduledDose> getDoses() {
        return doses;
    }

    public List<ScheduledDose> getDosesFor(DrugOrder order, Date startOfDay) {
        Date endOfDay = DateUtil.getEndOfDay(startOfDay);
        List<ScheduledDose> list = new ArrayList<ScheduledDose>();
        for (ScheduledDose candidate : doses) {
            if (candidate.getOrder().equals(order)
                    && OpenmrsUtil.compare(candidate.getScheduledDatetime(), startOfDay) >= 0
                    && OpenmrsUtil.compare(candidate.getScheduledDatetime(), endOfDay) <= 0) {
                list.add(candidate);
            }
        }
        return list;
    }

    public List<ScheduledDose> getDosesForDrug(Drug drug) {
        List<ScheduledDose> list = new ArrayList<ScheduledDose>();
        for (ScheduledDose candidate : doses) {
            if (candidate.getOrder().getDrug().equals(drug)) {
                list.add(candidate);
            }
        }
        return list;
    }

    public Map<DrugOrder, List<ScheduledDose>> getDosesByOrder() {
        Map<DrugOrder, List<ScheduledDose>> map = new HashMap<DrugOrder, List<ScheduledDose>>();
        for (ScheduledDose dose : doses) {
            List<ScheduledDose> list = map.get(dose.getOrder());
            if (list == null) {
                list = new ArrayList<ScheduledDose>();
                map.put(dose.getOrder(), list);
            }
            list.add(dose);
        }
        return map;
    }

    public List<Map.Entry<Drug, List<DrugOrder>>> getOrdersGroupedByDrug() {
        Map<Drug, List<DrugOrder>> grouped = new HashMap<Drug, List<DrugOrder>>();
        for (DrugOrder order : orders) {
            List<DrugOrder> list = grouped.get(order.getDrug());
            if (list == null) {
                list = new ArrayList<DrugOrder>();
                grouped.put(order.getDrug(), list);
            }
            list.add(order);
        }

        List<Map.Entry<Drug, List<DrugOrder>>> groups = new ArrayList<Map.Entry<Drug, List<DrugOrder>>>(grouped.entrySet());

        for (Map.Entry<Drug, List<DrugOrder>> group : groups) {
            Collections.sort(group.getValue(), new Comparator<DrugOrder>() {
                @Override
                public int compare(DrugOrder left, DrugOrder right) {
                    return OpenmrsUtil.compare(right.getEffectiveStartDate(), left.getEffectiveStartDate());
                }
            });
        }

        Collections.sort(groups, new Comparator<Map.Entry<Drug, List<DrugOrder>>>() {
            @Override
            public int compare(Map.Entry<Drug, List<DrugOrder>> left, Map.Entry<Drug, List<DrugOrder>> right) {
                return OpenmrsUtil.compare(earliestStartDate(right.getValue()), earliestStartDate(left.getValue()));
            }
        });

        return groups;
    }

    /**
     * @deprecated we decided to group by Drug instead of Concept, but I am leaving this code here just in case we need it later
     */
    @Deprecated
    public List<Map.Entry<Concept, List<DrugOrder>>> getOrdersGroupedByConcept() {
        Map<Concept, List<DrugOrder>> grouped = new HashMap<Concept, List<DrugOrder>>();
        for (DrugOrder order : orders) {
            List<DrugOrder> list = grouped.get(order.getConcept());
            if (list == null) {
                list = new ArrayList<DrugOrder>();
                grouped.put(order.getConcept(), list);
            }
            list.add(order);
        }

        List<Map.Entry<Concept, List<DrugOrder>>> groups = new ArrayList<Map.Entry<Concept, List<DrugOrder>>>(grouped.entrySet());

        for (Map.Entry<Concept, List<DrugOrder>> group : groups) {
            Collections.sort(group.getValue(), new Comparator<DrugOrder>() {
                @Override
                public int compare(DrugOrder left, DrugOrder right) {
                    return OpenmrsUtil.compare(right.getEffectiveStartDate(), left.getEffectiveStartDate());
                }
            });
        }

        Collections.sort(groups, new Comparator<Map.Entry<Concept, List<DrugOrder>>>() {
            @Override
            public int compare(Map.Entry<Concept, List<DrugOrder>> left, Map.Entry<Concept, List<DrugOrder>> right) {
                return OpenmrsUtil.compare(earliestStartDate(right.getValue()), earliestStartDate(left.getValue()));
            }
        });

        return groups;
    }

    private Date earliestStartDate(List<DrugOrder> list) {
        Date earliest = null;
        for (DrugOrder drugOrder : list) {
            if (earliest == null || OpenmrsUtil.compare(drugOrder.getEffectiveStartDate(), earliest) < 0) {
                earliest = drugOrder.getEffectiveStartDate();
            }
        }
        return earliest;
    }


}
