package com.caring.sass.ai.tools.pubmed;

import cn.hutool.core.collection.ListUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.StringJoiner;

/**
 * 封装 EFetch 请求参数的类。
 *
 * @author leizhi
 * @see <a href="https://www.ncbi.nlm.nih.gov/books/NBK25499/#chapter4.EInfo">
 */
@Slf4j
@Accessors(chain = true)
@Data
public class EFetchRequest {


    /**
     * 数据库名称，例如 "pubmed"。
     */
    private String db;
    /**
     * UID 列表，可以是单个 UID 或者多个 UID 组成的列表。
     */
    private List<String> ids;
    /**
     * 查询键，用于指定哪个 UID 列表将被用作 EFetch 的输入。
     */
    private String queryKey;
    /**
     * Web 环境字符串，通常从先前的 ESearch、EPost 或 ELink 调用的输出中获得。
     */
    private String webEnv;
    /**
     * 检索模式，例如 "text", "xml", "html"。
     */
    private String retMode;
    /**
     * 检索类型，例如 "abstract", "medline", "fasta"。
     */
    private String retType;
    /**
     * 要检索的第一个记录的顺序索引（默认为 0）。
     */
    private String retStart;
    /**
     * 要从输入集中检索的记录总数（默认为 20，最大为 10,000）。
     */
    private String retMax;
    /**
     * DNA 链，例如 "1" 表示正链，"2" 表示负链。
     */
    private String strand;
    /**
     * 要检索的第一个序列基的整数坐标。
     */
    private String seqStart;
    /**
     * 要检索的最后一个序列基的整数坐标。
     */
    private String seqStop;
    /**
     * 数据内容复杂度，例如 "0", "1", "2", "3", "4"。
     */
    private String complexity;

    /**
     * 构造一个新的 EFetch 请求。
     *
     * @param db  数据库名称
     * @param ids UID 列表
     */
    public EFetchRequest(String db, List<String> ids) {
        this.db = db;
        this.ids = ids;
    }

    /**
     * 发送 EFetch 请求并返回响应。
     *
     * @return 响应字符串
     * @throws Exception 发送请求时可能抛出的异常
     * @apiNote 以下是一个响应示例
     * <pre>
     * <PubmedArticleSet>
     *     <PubmedArticle>
     *         <MedlineCitation Status="MEDLINE" Owner="NLM" IndexingMethod="Automated">
     *             <PMID Version="1">39400727</PMID>
     *             <DateCompleted>
     *                 <Year>2024</Year>
     *                 <Month>10</Month>
     *                 <Day>14</Day>
     *             </DateCompleted>
     *             <DateRevised>
     *                 <Year>2024</Year>
     *                 <Month>10</Month>
     *                 <Day>14</Day>
     *             </DateRevised>
     *             <Article PubModel="Electronic">
     *
     *
     *                 <PublicationTypeList>
     *                     <PublicationType UI="D016428">Journal Article</PublicationType>
     *                 </PublicationTypeList>
     *                 <ArticleDate DateType="Electronic">
     *                     <Year>2024</Year>
     *                     <Month>10</Month>
     *                     <Day>14</Day>
     *                 </ArticleDate>
     *             </Article>
     *             <MedlineJournalInfo>
     *                 <Country>United States</Country>
     *                 <MedlineTA>Trop Anim Health Prod</MedlineTA>
     *                 <NlmUniqueID>1277355</NlmUniqueID>
     *                 <ISSNLinking>0049-4747</ISSNLinking>
     *             </MedlineJournalInfo>
     *             <CitationSubset>IM</CitationSubset>
     *             <MeshHeadingList>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D000818" MajorTopicYN="N">Animals</DescriptorName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D000465" MajorTopicYN="Y">Algorithms</DescriptorName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D034561" MajorTopicYN="N">Sheep, Domestic</DescriptorName>
     *                     <QualifierName UI="Q000254" MajorTopicYN="N">growth &amp; development</QualifierName>
     *                     <QualifierName UI="Q000502" MajorTopicYN="N">physiology</QualifierName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D001947" MajorTopicYN="N">Breeding</DescriptorName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D001835" MajorTopicYN="N">Body Weight</DescriptorName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D008954" MajorTopicYN="N">Models, Biological</DescriptorName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D008297" MajorTopicYN="N">Male</DescriptorName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D000822" MajorTopicYN="N">Animal Husbandry</DescriptorName>
     *                     <QualifierName UI="Q000379" MajorTopicYN="N">methods</QualifierName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D005260" MajorTopicYN="N">Female</DescriptorName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D012756" MajorTopicYN="N">Sheep</DescriptorName>
     *                     <QualifierName UI="Q000254" MajorTopicYN="N">growth &amp; development</QualifierName>
     *                 </MeshHeading>
     *                 <MeshHeading>
     *                     <DescriptorName UI="D003198" MajorTopicYN="N">Computer Simulation</DescriptorName>
     *                 </MeshHeading>
     *             </MeshHeadingList>
     *             <KeywordList Owner="NOTNLM">
     *                 <Keyword MajorTopicYN="N">Growth curves</Keyword>
     *                 <Keyword MajorTopicYN="N">Metaheuristic</Keyword>
     *                 <Keyword MajorTopicYN="N">Optimization</Keyword>
     *                 <Keyword MajorTopicYN="N">SAGAC algorithm</Keyword>
     *             </KeywordList>
     *         </MedlineCitation>
     *         <PubmedData>
     *             <History>
     *                 <PubMedPubDate PubStatus="received">
     *                     <Year>2024</Year>
     *                     <Month>5</Month>
     *                     <Day>10</Day>
     *                 </PubMedPubDate>
     *                 <PubMedPubDate PubStatus="accepted">
     *                     <Year>2024</Year>
     *                     <Month>10</Month>
     *                     <Day>1</Day>
     *                 </PubMedPubDate>
     *                 <PubMedPubDate PubStatus="medline">
     *                     <Year>2024</Year>
     *                     <Month>10</Month>
     *                     <Day>14</Day>
     *                     <Hour>12</Hour>
     *                     <Minute>23</Minute>
     *                 </PubMedPubDate>
     *                 <PubMedPubDate PubStatus="pubmed">
     *                     <Year>2024</Year>
     *                     <Month>10</Month>
     *                     <Day>14</Day>
     *                     <Hour>12</Hour>
     *                     <Minute>22</Minute>
     *                 </PubMedPubDate>
     *                 <PubMedPubDate PubStatus="entrez">
     *                     <Year>2024</Year>
     *                     <Month>10</Month>
     *                     <Day>14</Day>
     *                     <Hour>11</Hour>
     *                     <Minute>14</Minute>
     *                 </PubMedPubDate>
     *             </History>
     *             <PublicationStatus>epublish</PublicationStatus>
     *             <ArticleIdList>
     *                 <ArticleId IdType="pubmed">39400727</ArticleId>
     *                 <ArticleId IdType="doi">10.1007/s11250-024-04188-4</ArticleId>
     *                 <ArticleId IdType="pii">10.1007/s11250-024-04188-4</ArticleId>
     *             </ArticleIdList>
     *             <ReferenceList>
     *                 <Reference>
     *                     <Citation>Abd-Alameer AB, Al-Anbari NN (2021) Prediction of lambs growth from milk production and its composition in Awassi sheep and description of growth curve of non-linear function. In: IOP Conference Series: Earth and Environmental Science, 910. IOP Publishing, p 012060. https://doi.org/10.1088/1755-1315/910/1/012060</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Abebe A, Alemayehu K, Gizaw S, Johansson A (2022) Genetic and phenotypic parameters for growth and lamb survival traits of Farta and their crosses with Washera sheep in northwest Ethiopia: inputs to design of breeding programs. Cogent Food Agric :8. https://doi.org/10.1080/23311932.2022.2082043</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Abegaz S, Van Wyk JB, Olivier JJ (2010) Estimation of genetic and phenotypic parameters of growth curve and their relationship with early growth and productivity in Horro sheep. Archives Anim Breed 53(1):85&#x2013;94</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.5194/aab-53-85-2010</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Acero CHM (2002) Posicionamiento De La Carne Ovina en El Mercado Mundial. Memorias II taller sobre sistemas de producci&#xf3;n ovina del noreste y golfo de M&#xe9;xico. Universidad Aut&#xf3;noma de Tamaulipas, Tamaulipas</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Balan C, Kathiravan G, Thirunavukkarasu M, Jeichitra V (2017) Statistical analysis of growth performance in Mecheri Breed of Sheep. Birth 2:1&#x2013;36</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Bangar YC, Lawar VS, Nimase RG, Nimbalkar CA (2018) Comparison of non-linear growth models to describe the growth behaviour of Deccani sheep. Agricultural Res 7(4):490&#x2013;494. https://doi.org/10.1007/s40003-018-0338-2</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1007/s40003-018-0338-2</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Bangar YC, Magotra A, Malik BS, Malik ZS (2021) Evaluation of growth curve traits and associated genetic parameters in Harnali sheep. Small Ruminant Res 195:106314. https://doi.org/10.1016/j.smallrumres.2020.106314</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.smallrumres.2020.106314</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Barros NN, Vasconcelos VRD, Wander AE, Ara&#xfa;jo MRAD (2005) Efici&#xea;ncia bioecon&#xf4;mica de cordeiros F1 dorper x Santa In&#xea;s para produ&#xe7;&#xe3;o de carne. Pesquisa Agropecu&#xe1;ria Brasileira 40:825&#x2013;831. https://doi.org/10.1590/S0100-204X2005000800014</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1590/S0100-204X2005000800014</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Bathaei SS, Leroy PL (1998) Genetic and phenotypic aspects of the growth curve characteristics in Mehraban Iranian fat-tailed sheep. Small Ruminant Res 29(3):261&#x2013;269. https://doi.org/10.1016/S0921-4488(97)00142-9</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/S0921-4488(97)00142-9</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Behrem S (2021) Estimation of genetic parameters for pre-weaning growth traits in Central Anatolian Merino sheep. Small Ruminant Res 197:106319. https://doi.org/10.1016/J.SMALLRUMRES.2021.106319</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/J.SMALLRUMRES.2021.106319</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Benoit M, Mottet A (2023) Energy scarcity and rising cost: towards a paradigm shift for livestock. Agric Syst 205:e103585. https://doi.org/10.1016/j.agsy.2022.103585</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.agsy.2022.103585</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Benvenga MAC, N&#xe4;&#xe4;s IA, Lima NDDS, Pereira DF (2022) Hybrid metaheuristic algorithm for optimizing monogastric growth curve (pigs and broilers). AgriEngineering 4(4):1171&#x2013;1183. https://doi.org/10.3390/agriengineering4040073</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.3390/agriengineering4040073</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Boujenane I (2022) Comparison of non-linear models for describing the pre-weaning growth of Timahdite lambs and genetic and non-genetic effects for curve parameters. Small Ruminant Res 216:106800. https://doi.org/10.1016/j.smallrumres.2022.106800</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.smallrumres.2022.106800</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Castillo-Hern&#xe1;ndez G, Oliver-Gonz&#xe1;lez MR, Castillo-Hern&#xe1;ndez L et al (2022) Pre-weaning performance and commercial growth curve in Dorper, Katahdin, and Romanov crossed lambs in a highland zone from central Mexico. Trop Anim Health Prod 54:194. https://doi.org/10.1007/s11250-022-03202-x</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1007/s11250-022-03202-x</ArticleId>
     *                         <ArticleId IdType="pubmed">35655047</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Chai T, Draxler RR (2014) Root mean square error (RMSE) or mean absolute error (MAE)?&#x2013;Arguments against avoiding RMSE in the literature. Geosci Model Dev 7(3):1247&#x2013;1250</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.5194/gmd-7-1247-2014</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>de Andrade Souza L, Carneiro PLS, Malhado CHM et al (2011) Growth curves in morada nova sheep raised in the state of Bahia. Rev Bras Zootec 16. https://doi.org/10.1590/S1516-35982011000800011</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>De Paz CCP, Venturini GC, Contini E et al (2018) Non-linear models of Brazilian sheep in adjustment of growth curves. Czech J Anim Sci 8. https://doi.org/10.17221/87/2017-CJAS</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Deribe B, Tesema Z, Lakew M et al (2023) Growth and growth curve analysis in Dorper&#xd7; tumele crossbred sheep under a smallholder management system. Translational Anim Sci 7(1):txad034. https://doi.org/10.1093/tas/txad034</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1093/tas/txad034</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Dhakad GS, Saini S, Mallick PK et al (2022) Elucidating genotype by environment interaction over the growth trajectory of Malpura sheep in the semi-arid region of India through Random regression model. Small Rumin Res. https://doi.org/10.1016/j.smallrumres.2022.106791</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Dige MS, Rout PK, Singh MK et al (2021) Estimation of co (variance) components and genetic parameters for growth and feed efficiency traits in Jamunapari goat. Small Ruminant Res 196:106317. https://doi.org/10.1016/j.smallrumres.2021.106317</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.smallrumres.2021.106317</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Do Prado Paim T, Da Silva T, Martins AF et al (2013) Performance, survivability and carcass traits of crossbred lambs from five paternal breeds with local hair breed Santa In&#xea;s ewes. Small Ruminant Res 112(1&#x2013;3):28&#x2013;34</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.smallrumres.2012.12.024</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Dom&#xed;nguez-Viveros J, Canul-Santos E, Rodr&#xed;guez-Almeida FA et al (2019) Defining growth curves with non-linear models in seven sheep breeds in Mexico. Rev Mex Cienc Pecuarias 3. https://doi.org/10.22319/rmcp.v10i3.4804</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Esmailizadeh AK, Nemati M, Mokhtari MS (2012) Fattening performance of purebred and crossbred lambs from fat-tailed Kurdi ewes mated to four Iranian native ram breeds. Trop Anim Health Prod 44:217&#x2013;223. https://doi.org/10.1007/s11250-011-0001-4</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1007/s11250-011-0001-4</ArticleId>
     *                         <ArticleId IdType="pubmed">22076663</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Farhadian M, Rafat SA, and David I (2022) Determination of the best non-linear function and genetic parameter estimates of early growth in Romane lambs. J Livest Sci Technol. https://doi.org/10.22103/JLST.2022.16350.1327</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Godde C, Dizyee K, Ash A et al (2019) Climate change and variability impacts on grazing herds: insights from a system dynamics approach for semi-arid Australian rangelands. Glob Change Biol 25:3091&#x2013;3109. https://doi.org/10.1111/gcb.14669</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1111/gcb.14669</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Goliomytis M, Orfanos S, Panopoulou E, Rogdakis E (2006) Growth curves for body weight and carcass components, and carcass composition of the Karagouniko sheep, from birth to 720 d of age. Small Rumin Res 23. https://doi.org/10.1016/j.smallrumres.2005.09.021</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>G&#xf3;mez-V&#xe1;zquez A, De la Cruz-L&#xe1;zaro E, Pinos-Rodr&#xed;guez JM et al (2011) Growth performance and meat characteristics of hair lambs grazing star grass pasture without supplementation or supplemented with concentrate containing different levels of crude protein. Acta Agriculturae 61:115&#x2013;120</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Gompertz B (1825) On the nature of the function expressive of the law of human mortality, and on a new method of determining the value of life contingencies. Philos Trans Royal Soc 115:513&#x2013;585</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1098/rstl.1825.0026</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Gonz&#xe1;lez-Gardu&#xf1;o R, Torres-Hern&#xe1;ndez G, Castillo MA (2002) Crecimiento De Corderos Blackbelly entre El Nacimiento Y El peso final en El tr&#xf3;pico h&#xfa;medo De M&#xe9;xico. Vet M&#xe9;xico 33:443&#x2013;453</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Hamouda MB, Atti N (2011) Comparison of growth curves of lamb fat tail measurements and their relationship with body weight in Babarine sheep. Small Rumin Res 15. https://doi.org/10.1016/j.smallrumres.2010.10.001</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Hinojosa-Cu&#xe9;llar JA, Oliva-Hern&#xe1;ndez J, Torres-Hern&#xe1;ndez G, Segura-Correa JC (2013) Comportamiento productivo de corderos F1 Pelibuey x Blackbelly Y cruces con dorper y katahdin en un sistema de producci&#xf3;n del tr&#xf3;pico h&#xfa;medo de Tabasco, M&#xe9;xico. Arch De Med Vet 45(2):135&#x2013;143. https://doi.org/10.4067/S0301-732X2013000200004</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.4067/S0301-732X2013000200004</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Hodson TO (2022) Root-mean-square error (RMSE) or mean absolute error (MAE): when to use them or not. Geosci Model Dev 15(14):5481&#x2013;5487</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.5194/gmd-15-5481-2022</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Hojjati F, Hossein-Zadeh NG (2018) Comparison of non-linear growth models to describe the growth curve of Mehraban sheep. J Appl Anim Res 46(1):499&#x2013;504. https://doi.org/10.1080/09712119.2017.1348949</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1080/09712119.2017.1348949</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Hossein-Zadeh NG (2015) Modeling the growth curve of Iranian shall sheep using non-linear growth models. Small Rumin Res 44. https://doi.org/10.1016/j.smallrumres.2015.07.014</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Hossein-Zadeh NG (2017) Modeling growth curve in Moghani sheep: comparison of non-linear mixed growth models and estimation of genetic relationship between growth curve parameters. J Agricultural Sci 155(7):1150&#x2013;1159. https://doi.org/10.1017/S0021859617000326</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1017/S0021859617000326</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Iqbal F, Eyduran E, Raziq A, Ali M, Zil-e-Huma TC, Sevgenler H (2021) Modeling and predicting the growth of indigenous Harnai sheep in Pakistan: non-linear functions and MARS algorithm. Trop Anim Health Prod 1. https://doi.org/10.1007/s11250-021-02700-8</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Kannan TA, Jaganathan M, Ramanujam R, Msalya GM (2022) Assessment of growth and population structure revealed sufficient genetic diversity among lambs of Mecheri sheep in Tamil Nadu, India. Small Rumin Res 216:e106781. https://doi.org/10.1016/j.smallrumres.2022.106781</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Karthik D, Suresh J, Reddy YR et al (2021) Farming systems in sheep rearing: impact on growth and reproductive performance, nutrient digestibility, disease incidence and heat stress indices. PLoS ONE 16(1):e0244922. https://doi.org/10.1371/journal.pone.0244922</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1371/journal.pone.0244922</ArticleId>
     *                         <ArticleId IdType="pubmed">33439900</ArticleId>
     *                         <ArticleId IdType="pmc">7806139</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Kelman KR, Alston-Knox C, Pethick DW, Gardner GE (2022) Sire breed, litter size, and environment influence genetic potential for lamb growth when using sire breeding values. Animals 1. https://doi.org/10.3390/ani12040501</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Keskin I, Dag B, Sariyel V, Gokmen M (2009) Estimation of growth curve parameters in Konya Merino sheep. S Afr J Anim Sci 39(2). https://doi.org/10.4314/sajas.v39i2.44390</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Khaldari M, Azarfar A, Pahlavan R (2020) The size of fat tail does not have an effect on growth performance and carcass characteristics in Lori-Bakhtiari lambs. Small Ruminant Res 187:e106088. https://doi.org/10.1016/j.smallrumres.2020.106088</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.smallrumres.2020.106088</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Kirkpatrick S, Gelatti CD, Vecchi MP (1983) Optimization by simulated annealing. Sci New Ser 220:671&#x2013;680. https://science.sciencemag.org/content/220/4598/671</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Kizilaslan M, Arzik Y, Behrem S (2024) Genetic parameters for ewe lifetime productivity traits in Central Anatolian Merino sheep. Small Ruminant Res 233:107235. https://doi.org/10.46897/livestockstudies.1509590</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.46897/livestockstudies.1509590</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Kopuzlu S, Sezgin E, Esenbuga N, Bilgin OC (2014) Estimation of growth curve characteristics ofHemsin male and female sheep. J Appl Anim Res 42(2):228&#x2013;232. https://doi.org/10.1080/09712119.2013.842479</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1080/09712119.2013.842479</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Koushandeh A, Chamani M, Yaghobfar A et al (2019) Comparison of the accuracy of non-linear models and artificial neural network in the performance prediction of Ross 308 broiler chickens. Poult Sci J 7(2):151&#x2013;161</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Lenis-Valencia CP, Molin,a EJ, &#xc1;lvarez-Franco LA (2022) Productivity and growth curves using non-linear models in a cross between ovino de pelo colombiano x pelibuey. Revista UDC.A Actualidad and Divulgacion Cientifica. https://doi.org/10.31910/rudca.v25.n2.2022.1853</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Lupi TM, Nogales S, Le&#xf3;n JM, Barba C, Delgado JV (2015) Characterization of commercial and biological growth curves in the Segure&#xf1;a sheep breed. Animal 32. https://doi.org/10.1017/S1751731115000567</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Malhado CHM, Carneiro PLS, Affonso PRAM et al (2009) Growth curves in Dorper sheep crossed with the local Brazilian breeds, Morada Nova, Rabo Largo, and Santa In&#xea;s. Small Rumin Res 91. https://doi.org/10.1016/j.smallrumres.2009.04.006</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>McManus C, Evangelista C, Fernandes LAC et al (2003) Growth curves for Bergamasca sheep in the Brasilia Region. Rev Bras Zootec 44. https://doi.org/10.1590/s1516-35982003000500022</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Mitchell TM (1997) Machine learning. McGraw-Hill, New York</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Mohammadi Y, Mokhtari MS, Saghi DA, Shahdadi AR (2019) Modeling the growth curve in Kordi sheep: the comparison of non-linear models and estimation of genetic parameters for the growth curve traits. Small Rumin Res 13. https://doi.org/10.1016/j.smallrumres.2019.06.012</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Mokhtari MS, Borzi NK, Fozi MA, Behzadi MR (2019) Evaluation of non-linear models for genetic parameters estimation of growth curve traits in Kermani sheep. Trop Anim Health Prod 51(8):2203&#x2013;2212</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1007/s11250-019-01927-w</ArticleId>
     *                         <ArticleId IdType="pubmed">31127492</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Moreira RP, Pedrosa VB, Falc&#xe3;o PR et al (2016) Growth curves for Ile De France female sheep raised in feedlot. Semin Cienc Agrar 9. https://doi.org/10.5433/1679-0359.2016v37n1p303</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Myttenaere A, Golden B, Le Grand B, Rossi F (2016) Mean Absolute Percentage Error for regression models. Neurocomputing 192:38&#x2013;48. https://doi.org/10.1016/j.neucom.2015.12.114</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Owens FN, Dubeski P, Hanson CF (1993) Factors that alter the growth and development of ruminants. J Anim Sci 71(11):3138&#x2013;3150</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.2527/1993.71113138x</ArticleId>
     *                         <ArticleId IdType="pubmed">8270538</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Sall&#xe9; G, Deiss V, Marquis C, Tosser-Klopp G et al (2021) Genetic &#xd7; environment variation in sheep lines bred for divergent resistance to strongyle infection. Evol Appl 2. https://doi.org/10.1111/eva.13294</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Salles TT, Beijo LA, Nogueira DA et al (2020) Modelling the growth curve of Santa Ines sheep using bayesian approach. Livest Sci 239:104115. https://doi.org/10.1016/j.livsci.2020.104115</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.livsci.2020.104115</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Sarmento JLR, Regazzi AJ, Sousa WH et al (2006) Analysis of the growth curve of Santa Ines sheep. R Bras Zootec 35(2). https://doi.org/10.1590/S1516-35982006000200014</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Sharif N, Ali A, Mohsin I, Ahmad N (2021) Evaluation of non-linear models to define growth curve in Lohi sheep. Small Ruminant Res 205:106564. https://doi.org/10.1016/j.smallrumres.2021.106564</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.smallrumres.2021.106564</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Talebi R, Ghaffari M, Fabre S et al (2023) Comparison of the growth performance between pure Moghani sheep and crosses with Texel or Booroola sheep carrying major genes contributing to muscularity and prolificacy. Anim Biotechnol 1&#x2013;12. https://doi.org/10.1080/10495398.2023.2165933</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Thomas S, Venkatachalapathy RT, Anil KS, Suraj PT, Rojan PM (2022) Prediction of growth performance of Malabari goats using non-linear models. Indian J Small Ruminants 28(2):261&#x2013;266</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.5958/0973-9718.2022.00077.0</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Van der Merwe DA, Brand TS, Hoffman LC (2019) Application of growth models to different sheep breed types in South Africa. Small Rumin Res 14. https://doi.org/10.1016/j.smallrumres.2019.08.002</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Verhulst PF (1838) Notice sur la loi que la population suit dans son accroissement. Correspondence Math Phys Subj 10:113&#x2013;126</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Vogeler I, Vibart R, Cichota R (2017) Potential benefits of diverse pasture swards for sheep and beef farming. Agric Syst 3.&#xa0; https://doi.org/10.1016/j.agsy.2017.03.015</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Von Bertalanffy L (1950) An outline of general system theory. Br J Philos Sci 1:134&#x2013;165. https://doi.org/10.1093/bjps/I.2.134</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1093/bjps/I.2.134</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Waiz HA, Gautam L, Waiz SA (2019) Appraisal of growth curve in Sirohi goat using non-linear growth curve models. Trop Anim Health Prod 51:1135&#x2013;1140</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1007/s11250-018-01794-x</ArticleId>
     *                         <ArticleId IdType="pubmed">30604331</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Wen Y, Li S, Bao G, Wang J et al (2022) Comparative transcriptome analysis reveals the mechanism associated with dynamic changes in meat quality of the longissimus thoracis muscle in tibetan sheep at different growth stages. Front Vet Sci. https://doi.org/10.3389/fvets.2022.926725</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Willmott C, Matsuura K (2005) Advantages of the Mean Absolute Error (MAE) over the Root Mean Square Error (RMSE) in assessing average model performance. Clim Res 30:79&#x2013;82</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Willmott CJ, Matsuura K, Robeson SM (2009) Ambiguities inherent in sums-of-squares-based error statistics. Atmos Environ 43:749&#x2013;752</Citation>
     *                     <ArticleIdList>
     *                         <ArticleId IdType="doi">10.1016/j.atmosenv.2008.10.005</ArticleId>
     *                     </ArticleIdList>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Xu S, Wang Y, Lu P (2015) Improved imperialist competitive algorithm with mutation operator for continuous optimization problems. Neural Comput Appl. https://doi.org/10.1007/s00521-015-2138-y</Citation>
     *                 </Reference>
     *                 <Reference>
     *                     <Citation>Zhang Y, Rao Y, Zhou M (2007) GASA Hybrid algorithm applied in airline crew rostering system. Tsinghua Sci Technol 12. https://doi.org/10.1016/S1007-0214(07)70120-7</Citation>
     *                 </Reference>
     *             </ReferenceList>
     *         </PubmedData>
     *     </PubmedArticle>
     * </PubmedArticleSet>
     * </pre>
     */
    public String sendRequest() throws Exception {
        String baseUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi";
        StringJoiner params = new StringJoiner("&");

        params.add("db=" + db);
        params.add("id=" + String.join(",", ids));

        if (queryKey != null) {
            params.add("query_key=" + queryKey);
        }
        if (webEnv != null) {
            params.add("WebEnv=" + webEnv);
        }
        if (retMode != null) {
            params.add("retmode=" + retMode);
        }
        if (retType != null) {
            params.add("rettype=" + retType);
        }
        if (retStart != null) {
            params.add("retstart=" + retStart);
        }
        if (retMax != null) {
            params.add("retmax=" + retMax);
        }
        if (strand != null) {
            params.add("strand=" + strand);
        }
        if (seqStart != null) {
            params.add("seq_start=" + seqStart);
        }
        if (seqStop != null) {
            params.add("seq_stop=" + seqStop);
        }
        if (complexity != null) {
            params.add("complexity=" + complexity);
        }

        String reqUrl = baseUrl + "?" + params;
        System.out.println(reqUrl);
        log.debug("EFetchRequest url is {}", reqUrl);

        URL url = new URL(reqUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    /**
     * 主方法，用于测试 EFetch 请求。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            // 示例 UID 列表
            String string = "22099613,7887360,20890812,20598067,28658067,18206714,9161315,32451131,24602682,24314710,36766723,17433446,35658882,36922168,9689338,9549458,28463953,22032783,38086478,12517578";
            String[] split = string.split(",");
            List<String> ids = ListUtil.of(split);
            EFetchRequest request = new EFetchRequest("pubmed", ids);
            String response = request.sendRequest();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
