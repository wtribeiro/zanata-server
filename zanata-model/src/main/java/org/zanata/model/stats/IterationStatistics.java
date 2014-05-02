/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.model.stats;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;
import org.zanata.common.LocaleId;
import org.zanata.model.HProjectIteration;
import org.zanata.model.ModelEntityBase;
import org.zanata.model.type.LocaleIdType;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Statistics record for a project version and a language.
 *
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
        "project_iteration_id", "locale_id" }))
@TypeDef(name = "localeIdType", typeClass = LocaleIdType.class,
        defaultForType = LocaleId.class)
@Access(AccessType.FIELD)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class IterationStatistics extends ModelEntityBase {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "project_iteration_id", nullable = false)
    private HProjectIteration projectIteration;

    @Column("locale_id")
    @NotNull
    private LocaleId localeId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "IterationStatistics_StatCounts",
            joinColumns = @JoinColumn(name = "iteration_stats_id"),
            inverseJoinColumns = @JoinColumn(name = "stat_count_id"))
    private Set<StatCount> stats;
}
