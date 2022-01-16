from opentuner.search import technique
from functools import cmp_to_key
import sys
class GridSearch(technique.SequentialSearchTechnique):
  def main_generator(self):

    objective   = self.objective
    driver      = self.driver
    manipulator = self.manipulator

    inital={'opt_level':0,'blockSize':8}

    # start at a random position
    center = driver.get_configuration(inital)
    yield center
    # print("center type",type(center))
    best=center
    yield best
  

    while True:
      points = list()
      for param in manipulator.parameters(center.data):
        if param.is_primitive():
          print('center',center.data)
          # get current value of param, scaled to be in range [0.0, 1.0]
          # unit_value = param.get_unit_value(center.data)
          # print(unit_value)
          # 2 16 0.6250062503125157
          # 2 32 0.6250062503125157
          # 3 64 0.8750187509375469
          # steps=param.get_unit_value(1.0)
          # print(steps)
          if int(center.data['blockSize'])<128:
            next_cfg=manipulator.copy(center.data)
            
            # print(type(next_cfg['blockSize']))
            blocksize_next=int(next_cfg['blockSize'])*2

          
            next_cfg['blockSize']=str(blocksize_next)
            # print('next_cfg',next_cfg)
            unit_value = param.get_unit_value(next_cfg)
            # print("unit_value",unit_value)
            # param.set_unit_value(next_cfg,unit_value)
            next_cfg=driver.get_configuration(next_cfg)
            yield next_cfg
            # print(type(next_cfg))
            points.append(next_cfg)
          elif int(center.data['opt_level'])<3:
            go_cfg=manipulator.copy(center.data)
            opt_level_next=int(go_cfg['opt_level'])+1
            go_cfg['opt_level']=opt_level_next
            go_cfg['blockSize']=str(8)
            # print('go_cfg',go_cfg)
            go_cfg=driver.get_configuration(go_cfg)
            yield go_cfg
            points.append(go_cfg)
          
          else:
            points.append(center)
            sys.exit()

   
      points.sort(key=cmp_to_key(objective.compare))
      # print('points',points)
      # print('points[0]',points[0])
      center=points[0]
      if objective.lt(points[0], best):
       
        best = points[0]
       

# register our new technique in global list
technique.register(GridSearch())