package kr.co.theplay.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface AlcoholTagRepository extends JpaRepository <AlcoholTag, Long> {

    List<AlcoholTag> findByPost(Post post);

    @Query(value = "select count(*) as cnt, name " +
            "from alcohol_tag at " +
            "inner join post p on at.post_id = p.id " +
            "inner join post_like pl on p.id = pl.post_id " +
            "where recipe_yn = 'Y' " +
            "group by name " +
            "order by cnt desc ",
    countQuery = "select count(distinct name) " +
            "from alcohol_tag at " +
            "inner join post p on at.post_id = p.id " +
            "inner join post_like pl on p.id = pl.post_id " +
            "where recipe_yn = 'Y' ",
    nativeQuery = true )
    Page<Object []> findPopularTags(Pageable pageable);

    @Query(
            value = "select at.post_id, count(pl.id) as cnt, pi.file_path " +
                    "from alcohol_tag at " +
                    "inner join post p on at.post_id = p.id " +
                    "left outer join post_like pl on p.id = pl.post_id " +
                    "inner join post_image pi on p.id = pi.post_id " +
                    "where at.recipe_yn = 'Y' and at.name = :tagName and pi.number = 0 " +
                    "group by p.id " +
                    "order by cnt desc " +
                    "limit 10 ",
            nativeQuery = true
    )
    List <Object [] > find10PopularImagesByAlcoholTagName(@Param("tagName") String tagName);

    @Query("select a from AlcoholTag a where a.post.id = :postId and a.recipeYn = 'Y'")
    AlcoholTag findRecipesByPostId(@Param("postId") Long postId);

    @Query(
            value = "select count(pl.id) as likeCnt, p.id " +
                    "from post p " +
                    "left outer join post_like pl on p.id = pl.post_id " +
                    "inner join alcohol_tag a on p.id = a.post_id " +
                    "where a.name = :tagName and a.recipe_yn = 'Y' " +
                    "group by p.id " +
                    "order by likeCnt desc ",

            countQuery = "select count(distinct p.id) " +
                    "from post p\n" +
                    "left outer join post_like pl on p.id = pl.post_id " +
                    "inner join alcohol_tag a on p.id = a.post_id " +
                    "where a.name = :tagName and a.recipe_yn = 'Y' ",
            nativeQuery = true
    )
    Page<Object[]> findByNameOrderByLikeCnt(@Param("tagName") String tagName, Pageable page);
}
