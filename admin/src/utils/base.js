const base = {
    get() {
        return {
            url : "http://localhost:8080/springbooths7l2/",
            name: "springbooths7l2",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/springbooths7l2/front/dist/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "基于Springboot“文趣阁”阅读平台的设计与实现"
        } 
    }
}
export default base
