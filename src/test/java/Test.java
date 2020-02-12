import com.spring.entity.Article;
import com.spring.repositories.ArticleRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class Test {
    //可注入
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ElasticsearchTemplate template;

    /**
     * 创建索引
     * @throws Exception
     */
    @org.junit.Test
    public void createIndex() throws Exception {
        //创建索引并配置映射关系
        //实体类中配置好了映射
        template.createIndex(Article.class);
    }
    /**
     * 添加，更新文档
     * 更新就是先删除再增加
     * 只需id一致，再添加一次即可
     */
    @org.junit.Test
    public void addDocument() {
        Article article = new Article();

        for (int i = 0; i < 10 ; i++) {
            article.setId(i);
            article.setTitle("北京朝阳：舍小家为大家 朝阳群众家庭总动员共抗疫情" + i);
            article.setContent(i + "在北京朝阳区，不少朝阳群众和家人共同站在抗疫一线，他们有的是夫妻，有的是父子，还有的是婆媳，他们共同勉励、相互扶持，舍小家、为大家，成为抗疫队伍中的一个个“佳话”。");
            //把文档写入索引库
            articleRepository.save(article);
        }
        article.setId(2);
        article.setTitle("北京朝阳：舍小家为大家 朝阳群众家庭总动员共抗疫情2");
        article.setContent("在北京朝阳区，不少朝阳群众和家人共同站在抗疫一线，他们有的是夫妻，有的是父子，还有的是婆媳，他们共同勉励、相互扶持，舍小家、为大家，成为抗疫队伍中的一个个“佳话”。");
        //把文档写入索引库
        articleRepository.save(article);
    }

    /**
     * 删除文档
     * @throws Exception
     */
    @org.junit.Test
    public void delete() throws Exception {
        articleRepository.deleteById(1l);


    }

    /**
     * 查询所有
     * @throws Exception
     */
    @org.junit.Test
    public void findAll() throws Exception {
        Iterable<Article> all = articleRepository.findAll();
        all.forEach(t -> System.out.println(t));

    }

    /**
     * 查询通过id
     * @throws Exception
     */
    @org.junit.Test
    public void findById() throws Exception {
        Optional<Article> article = articleRepository.findById(1l);
        Article article1 = article.get();
        System.out.println(article);
    }

    /**
     * 自定义的查询jpa类似
     * @throws Exception
     */
    @org.junit.Test
    public void findByTitle() throws Exception {
        /*List<Article> title = articleRepository.findByTitle("北京");
        title.forEach(t -> System.out.println(t));*/

        /*List<Article> list = articleRepository.findByTitleAndContent("北京", "北京");
        System.out.println(list.size());*/

        //分页
        Pageable pageable = PageRequest.of(1, 5);
        List<Article> list = articleRepository.findByTitleOrContent("北京", "北京", pageable);
        list.forEach(t -> System.out.println(t));
        //若不设置分页信息，默认自带分页，每页显示十条
        //若设置需要添加Pageable pageable = PageRequest.of(1, 5);
        //默认从零页开始
    }
//springdata自带的查询会自带分词，词之间不连续，无法语句查询
    /**
     * 原生的查询方法
     * @throws Exception
     */
    @org.junit.Test
    public void findByNativeQuery() throws Exception {
        //创建一个SearchQuery对象
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery("有的是父子").defaultField("content"))
                .withPageable(PageRequest.of(0, 5))
                .build();
        //设置查询条件，此处可以使用QueryBuilders创建多种查询
        List<Article> articles = template.queryForList(query, Article.class);
        articles.forEach(t -> System.out.println(t));
    }
}
