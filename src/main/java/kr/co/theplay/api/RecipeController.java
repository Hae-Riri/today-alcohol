package kr.co.theplay.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.co.theplay.dto.post.PostResDto;
import kr.co.theplay.dto.recipe.PopularRecipeDto;
import kr.co.theplay.dto.recipe.UserRecipeResDto;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.ListResult;
import kr.co.theplay.service.api.common.model.SingleResult;
import kr.co.theplay.service.post.PostService;
import kr.co.theplay.service.recipe.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"104. Recipe (레시피)"})
@RequestMapping(value = "/v1")
@Slf4j(topic = "RecipeLogger")
@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final ResponseService responseService;
    private final RecipeService recipeService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "인기 레시피 조회", notes = "인기 레시피의 태그이름들과 첫 레시피 재료, 이미지 10개를 조회한다.")
    @GetMapping(value = "/popular-recipes")
    public ResponseEntity<SingleResult<Page<PopularRecipeDto>>> getPopularRecipes(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PopularRecipeDto> popularRecipeDtos = recipeService.getPopularRecipes(number, size);
        SingleResult<Page<PopularRecipeDto>> result = responseService.getSingleResult(popularRecipeDtos);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "유저 나의 레시피 목록 불러오기", notes = "유저 나의 레시피 (저장한 레시피) 목록을 페이징으로 가져온다")
    @GetMapping(value = "/user/recipes")
    public ResponseEntity<SingleResult<Page<UserRecipeResDto>>> getUserRecipes(@RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<UserRecipeResDto> userRecipeResDtos = recipeService.getUserRecipes(email, number, size);
        SingleResult<Page<UserRecipeResDto>> result = responseService.getSingleResult(userRecipeResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "유저 나의 레시피 검색 가져오기", notes = "유저 나의 레시피 (저장한 레시피) 검색내역을 가져온다")
    @GetMapping(value = "/user/recipe")
    public ResponseEntity<ListResult<UserRecipeResDto>> getUserRecipe(@RequestParam("recipeName") String recipeName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        List<UserRecipeResDto> userRecipeResDtos = recipeService.getUserSearchRecipe(email, recipeName);
        ListResult<UserRecipeResDto> result = responseService.getListResult(userRecipeResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "선택 유저 나의 레시피 가져오기", notes = "선택한 유저의 나의 레시피 (저장한 레시피) 를 페이징으로 가져온다")
    @GetMapping(value = "/user/{userId}/recipes")
    public ResponseEntity<SingleResult<Page<UserRecipeResDto>>> getOtherUsersRecipes(@PathVariable Long userId,
                                                                                     @RequestParam("pageNumber") int number,
                                                                                     @RequestParam("pageSize") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<UserRecipeResDto> userRecipeResDtos = recipeService.getOtherUserRecipes(email, userId, number, size);
        SingleResult<Page<UserRecipeResDto>> result = responseService.getSingleResult(userRecipeResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "선택 유저 나의 레시피 검색 가져오기", notes = "선택한 유저의 나의 레시피 (저장한 레시피) 검색내역을 가져온다")
    @GetMapping(value = "/user/{userId}/recipe")
    public ResponseEntity<ListResult<UserRecipeResDto>> getOtherUserRecipe(@PathVariable Long userId,
                                                                           @RequestParam("recipeName") String recipeName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        List<UserRecipeResDto> userRecipeResDtos = recipeService.getOtherUserSearchRecipe(email, userId, recipeName);
        ListResult<UserRecipeResDto> result = responseService.getListResult(userRecipeResDtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-ACCESS-TOKEN", value = "Access Token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "인기 레시피 모두 보기", notes = "인기 레시피 중 특정 태그에 대해 모든 레시피를 조회한다.")
    @GetMapping(value = "/popular-recipes/{tagName}")
    public ResponseEntity<SingleResult<Page<PostResDto>>> getPopularRecipesByTagName(
            @PathVariable String tagName, @RequestParam("pageNumber") int number, @RequestParam("pageSize") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email.equals("anonymousUser")) {
            throw new CommonConflictException("accessException");
        }

        Page<PostResDto> postResDtos = recipeService.getPopularRecipesByTagName(email, tagName, number, size);
        SingleResult<Page<PostResDto>> result = responseService.getSingleResult(postResDtos);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
