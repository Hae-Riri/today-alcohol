package kr.co.theplay.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.theplay.domain.user.User;
import kr.co.theplay.dto.follow.BlockedUserDto;
import kr.co.theplay.dto.follow.FollowUserDto;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import kr.co.theplay.service.api.common.model.ListResult;
import kr.co.theplay.service.follow.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"102. Following (팔로우, 차단)"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "FollowingLogger")
@RequiredArgsConstructor
@RestController
public class FollowController {

    private final ResponseService responseService;
    private final FollowService followService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로우하기", notes = "특정 회원을 팔로우한다.")
    @PostMapping(value = "/user/following/{userId}")
    public ResponseEntity<CommonResult> followUser(@PathVariable Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        followService.followUser(email, userId);

        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로잉 목록 조회", notes = "로그인한 회원이 팔로잉하는 회원 목록을 조회한다.")
    @GetMapping(value = "/user/followings")
    public ResponseEntity<ListResult<FollowUserDto>> getFollowings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        List<FollowUserDto> followUserDtos = followService.getFollowings(email);
        ListResult<FollowUserDto> result = responseService.getListResult(followUserDtos);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔오워 목록 조회", notes = "로그인한 회원의 팔로워 목록을 조회한다")
    @GetMapping(value = "/user/followers")
    public ResponseEntity<ListResult<FollowUserDto>> getFollowers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }
        List<FollowUserDto> followUserDtos = followService.getFollowers(email);
        ListResult<FollowUserDto> result = responseService.getListResult(followUserDtos);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "선택 유저 팔로워 목록 조회", notes = "로그인한 회원의 팔로워 목록을 조회한다")
    @GetMapping(value = "/user/{userId}/followers")
    public ResponseEntity<ListResult<FollowUserDto>> getOtherUserFollowers(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }
        List<FollowUserDto> followUserDtos = followService.getOtherUserFollower(userId);
        ListResult<FollowUserDto> result = responseService.getListResult(followUserDtos);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로워 삭제", notes = "회원의 팔로워를 삭제한다")
    @DeleteMapping(value = "/user/followers/{userId}")
    public ResponseEntity<CommonResult> deleteFollower(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        followService.deleteFollower(email, userId);

        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로잉 취소", notes = "팔로잉을 취소한다")
    @DeleteMapping(value = "/user/followings/{userId}")
    public ResponseEntity<CommonResult> deleteFollowing(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        followService.deleteFollowing(email, userId);

        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "팔로워 차단", notes = "팔로워를 삭제 & 차단한다")
    @PostMapping(value = "/user/followers/block/{userId}")
    public ResponseEntity<CommonResult> blockFollower(@PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        // 팔로잉 삭제
        followService.blockFollower(email, userId);

        return new ResponseEntity<>(responseService.getSuccessResult(), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "차단한 이용자 목록 조회", notes = "차단한 이용자 목록을 조회한다.")
    @GetMapping(value = "/user/followers/blocks")
    public ResponseEntity<ListResult<BlockedUserDto>> getBlockedUsers(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        if(email.equals("anonymousUser")){
            throw new CommonConflictException("accessException");
        }
        List<BlockedUserDto> blockedUserDtos = followService.getBlockedUsers(email);
        ListResult<BlockedUserDto> result = responseService.getListResult(blockedUserDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
