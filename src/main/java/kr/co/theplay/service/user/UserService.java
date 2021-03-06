package kr.co.theplay.service.user;

import kr.co.theplay.api.config.security.JwtTokenProvider;
import kr.co.theplay.domain.follow.Follow;
import kr.co.theplay.domain.follow.FollowRepository;
import kr.co.theplay.domain.post.*;
import kr.co.theplay.domain.user.*;
import kr.co.theplay.dto.user.*;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonConflictException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final PostLikeRepository postLikeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "todaysalcoholservice@gmail.com";

    @Transactional
    public String signUp(SignUpDto signUpDto) {

//        User user = SignUpDtoMapper.INSTANCE.toEntity(signUpDto);

        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(signUpDto.getPassword())
                .nickname(signUpDto.getNickname())
                .build();

        //이미 가입한 회원인지 email 확인
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new CommonConflictException("userDuplication");
        }

        //닉네임 중복 확인
        if (userRepository.findByNickname(signUpDto.getNickname()).isPresent()) {
            throw new CommonConflictException("nicknameDuplication");
        }

        userRepository.save(user);

        //Role 생성
        UserRole userRole = UserRole.builder().user(user).roleName("ROLE_USER").build();
        userRoleRepository.save(userRole);

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        return jwtTokenProvider.createToken(String.valueOf(user.getId()), roles);
    }

    @Transactional
    public void updateUserPassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonConflictException("userNotFound"));
        user.updateUserPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }


    public void signIn(SignInDto signInDto) {
        // 존재하는 사용자인지 확인
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new CommonBadRequestException("passwordDenied");
        }
    }

    public String getLoginToken(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        return jwtTokenProvider.createToken(String.valueOf(user.getId()), roles);
    }

    @Transactional
    public void updateUserNickname(UserChangeNicknameDto userChangeNicknameDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        if (userRepository.findByNickname(userChangeNicknameDto.getNickname()).isPresent()) {
            throw new CommonConflictException("nicknameDuplication");
        }

        user.updateUserNickname(userChangeNicknameDto.getNickname());
        userRepository.save(user);
    }

    @Transactional
    public UserSendEmailDto createMailAndChangePassword(String email) {
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new CommonConflictException("userNotFound");
        }

        String tempPassword = getTempPassword();
        UserSendEmailDto userSendEmailDto = new UserSendEmailDto();
        userSendEmailDto.setEmail(email);
        userSendEmailDto.setTitle("[오늘 한 주] " + email + " 님의 '오늘 한 주' 임시 비밀번호 안내 이메일입니다.");
        userSendEmailDto.setMessage("안녕하세요, 오늘 한 주 임시 비밀번호 안내 관련 이메일 입니다.\n" + "[" + email
                + "] 님의 임시 비밀번호는 \n Password: [" + tempPassword + "] 입니다.\n" + "해당 비밀번호로 로그인 하시고 비밀번호를 변경하여 주시기 바랍니다.");
        updateUserPassword(email, tempPassword);
        return userSendEmailDto;
    }

    // 임시 비밀번호 생성기
    public String getTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    public void sendEmail(UserSendEmailDto userSendEmailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userSendEmailDto.getEmail());
        message.setFrom(FROM_ADDRESS);
        message.setSubject(userSendEmailDto.getTitle());
        message.setText(userSendEmailDto.getMessage());
        mailSender.send(message);
    }

    @Transactional
    public UserChangePrivacyResDto changePrivacyYn(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        String privacyYn = user.changePrivacyYn();
        UserChangePrivacyResDto userChangePrivacyResDto = UserChangePrivacyResDto.builder().privacyYn(privacyYn).build();
        return userChangePrivacyResDto;
    }

    @Transactional
    public void changePassword(UserChangePasswordDto userChangePasswordDto, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        // 비밀번호 입력 오류
        if (!passwordEncoder.matches(userChangePasswordDto.getPassword(), user.getPassword())) {
            throw new CommonConflictException("passwordDenied");
        }

        //valid를 통과했다면 newPassword, confirmPassword 비교
        if (!userChangePasswordDto.getNewPassword().equals(userChangePasswordDto.getConfirmPassword())) {
            throw new CommonBadRequestException("passwordNotMatched");
        }

        // 이전 비밀번호와 중복인지
        if (passwordEncoder.matches(userChangePasswordDto.getNewPassword(), user.getPassword())) {
            throw new CommonConflictException("passwordDuplication");
        }

        updateUserPassword(email, userChangePasswordDto.getNewPassword());
    }

    public UserSettingsDto getUserSettings(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        UserSettingsDto userSettingsDto = UserSettingsDto
                .builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
        //UserSettingsDto userSettingsDto = UserSettingsDtoMapper.INSTANCE.toDto(user);
        return userSettingsDto;
    }

    public List<RandomNicknameDto> getRandomNickname() {
        String[] nicknameSetA = new String[]{
                "멋쟁이", "우아한", "상큼한", "얼음과", "차가운", "나만의", "신나는"
        };
        String[] nicknameSetB = new String[]{
                "보드카", "소주", "맥주", "수제맥주", "위스키", "와인", "칵테일", "바텐더", "사케", "막걸리"
        };

        List<RandomNicknameDto> randomNicknameDto = new ArrayList<>();
        boolean[] boolA = new boolean[10];
        boolean[] boolB = new boolean[10];
        int idx1 = 0, idx2 = 0;
        for (int i = 0; i < 2; i++) {
            String randomNickname = "";
            idx1 = (int) (nicknameSetA.length * Math.random());
            idx2 = (int) (nicknameSetB.length * Math.random());
            while (boolA[idx1] == true)
                idx1 = (int) (nicknameSetA.length * Math.random());
            while (boolB[idx2] == true)
                idx2 = (int) (nicknameSetB.length * Math.random());
            boolA[idx1] = boolB[idx2] = true;
            randomNickname += (nicknameSetA[idx1] + " " + nicknameSetB[idx2]);
            RandomNicknameDto randomNicknameDto1 = RandomNicknameDto.builder().nickname(randomNickname).build();
            randomNicknameDto.add(randomNicknameDto1);
        }

        return randomNicknameDto;
    }

    public UserMainInfoDto getUserMainInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        List<Post> postCount = postRepository.findByUserEmail(email);
        List<Follow> followerCount = followRepository.findFollowersByUser(email);
        List<PostLike> likesCount = postLikeRepository.findByUserEmail(email);
        List<UserRecipe> recipesCount = userRecipeRepository.getUserRecipeByUser(user);
        UserMainInfoDto userMainInfoDto = UserMainInfoDto.builder().nickname(user.getNickname()).posts(postCount.stream().count()).followers(followerCount.stream().count()).likes(likesCount.stream().count()).recipes(recipesCount.stream().count()).followingYn("N").build();
        return userMainInfoDto;
    }

    public UserMainInfoDto getOtherUserIngo(String email, Long userId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        User otherUser = userRepository.findById(userId).orElseThrow(() -> new CommonNotFoundException("userNotFound"));

        // 게시물은 가져온다
        List<Post> postCount = postRepository.findByUserEmail(otherUser.getEmail());

        UserMainInfoDto userMainInfoDto = new UserMainInfoDto();

        // 혹시나.. 토큰 사용자가 나의 정보를 이 API 로 접근하면? 예외처리도 괜찮지만 그냥 비공개 상관없이 보여주도록..
        if (otherUser.getPrivacyYn().equals("N") || otherUser.getEmail().equals(email)) { // 비공개 계정이 아니라면
            List<Follow> followerCount = followRepository.findFollowersByUser(otherUser.getEmail());
            List<PostLike> likesCount = postLikeRepository.findByUserEmail(otherUser.getEmail());
            List<UserRecipe> recipesCount = userRecipeRepository.getUserRecipeByUser(otherUser);
            if(followRepository.existsFollowByUserAndUserFollow(user, otherUser))
                userMainInfoDto = UserMainInfoDto.builder().nickname(otherUser.getNickname()).posts(postCount.stream().count()).followers(followerCount.stream().count()).likes(likesCount.stream().count()).recipes(recipesCount.stream().count()).followingYn("Y").build();
            else
                userMainInfoDto = UserMainInfoDto.builder().nickname(otherUser.getNickname()).posts(postCount.stream().count()).followers(followerCount.stream().count()).likes(likesCount.stream().count()).recipes(recipesCount.stream().count()).followingYn("N").build();
        } else {
            Long empty = (long) -1;
            if(followRepository.existsFollowByUserAndUserFollow(user, otherUser))
                userMainInfoDto = UserMainInfoDto.builder().nickname(otherUser.getNickname()).posts(postCount.stream().count()).followers(empty).likes(empty).recipes(empty).followingYn("Y").build();
            else
                userMainInfoDto = UserMainInfoDto.builder().nickname(otherUser.getNickname()).posts(postCount.stream().count()).followers(empty).likes(empty).recipes(empty).followingYn("N").build();
        }

        return userMainInfoDto;
    }

    public UserChangePrivacyResDto getUserPrivacy(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        UserChangePrivacyResDto userChangePrivacyResDto = UserChangePrivacyResDto.builder().privacyYn(user.getPrivacyYn()).build();
        return userChangePrivacyResDto;
    }

    public UserAlarmNewYnDto getNewAlarmYn(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CommonNotFoundException("userNotFound"));
        UserAlarmNewYnDto userAlarmNewYnDto;
        if(user.getNewAlarmYn().equals("Y")){
            userAlarmNewYnDto = UserAlarmNewYnDto.builder().newAlarmYn("Y").build();
        }else{
            userAlarmNewYnDto = UserAlarmNewYnDto.builder().newAlarmYn("N").build();
        }
        return userAlarmNewYnDto;
    }
}
