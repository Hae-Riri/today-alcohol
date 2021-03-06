package kr.co.theplay.service.user;

import javax.annotation.Generated;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.dto.user.UserSettingsDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-07-04T17:14:56+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_191-1-ojdkbuild (Oracle Corporation)"
)
public class UserSettingsDtoMapperImpl implements UserSettingsDtoMapper {

    @Override
    public User toEntity(UserSettingsDto dto) {
        if ( dto == null ) {
            return null;
        }

        String email = null;
        String nickname = null;

        email = dto.getEmail();
        nickname = dto.getNickname();

        Long id = null;
        String password = null;
        String privacyYn = null;
        String newAlarmYn = null;

        User user = new User( id, email, password, nickname, privacyYn, newAlarmYn );

        return user;
    }

    @Override
    public UserSettingsDto toDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserSettingsDto userSettingsDto = new UserSettingsDto();

        return userSettingsDto;
    }
}
