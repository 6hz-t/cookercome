package wit.backendcooker.Controller;

/*
 * @author ：jee
 * @date ：2026/3/1010:40
 * @version: 1.0
 --------------------------
 */

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wit.backendcooker.Result.Result;
import wit.backendcooker.VO.ChefInfoVo;

@Tag(name = "厨师信息")
@RestController
@RequestMapping("/api/chef")
public class ChefInfoController {
 @Operation( summary = "获取厨师信息")
 @PostMapping("/getChefInfo")
 public Result<ChefInfoVo> getChefInfo(){

  //todo 获取厨师信息
     return Result.success(new ChefInfoVo());
 }


 @Operation( summary = "更新厨师信息")
 @PostMapping("/updateChefInfo")
 public Result<String> updateChefInfo(@RequestBody ChefInfoVo chefInfoVo){

  //todo 更新厨师信息
  return Result.success("更新成功");
 }


 //部分更新
 @Operation( summary = "部分更新厨师信息")
 @PostMapping("/patchChefInfo")
 public Result<String> patchChefInfo(@RequestBody ChefInfoVo chefInfoVo){

  //todo 部分更新厨师信息
  return Result.success("更新成功");
 }





















}
