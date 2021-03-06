package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserChangeNicknameDto {
    @ApiModelProperty(value = "닉네임", dataType = "String", required = true, example = "오늘 한 주")
    private String nickname;

    @Builder
    public UserChangeNicknameDto(String nickname) {
        this.nickname = nickname;
    }
}
