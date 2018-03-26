package org.hejwo.gobybus.busstopcrawler.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hejwo.gobybus.commons.domain.customlocaltime.CustomLocalTime;

@NoArgsConstructor
@Getter
@AllArgsConstructor(staticName = "from")
@Builder
@EqualsAndHashCode
@ToString
public class Timetable {

    private String line;
    private String brigade;
    private String direction;
    private String route;
    private CustomLocalTime localTime;

}
