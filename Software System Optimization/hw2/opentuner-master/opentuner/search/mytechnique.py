from opentuner.search import technique

from functools import cmp_to_key
class BasicPatternSearch(technique.SequentialSearchTechnique):
  def main_generator(self):

    objective   = self.objective
    driver      = self.driver
    manipulator = self.manipulator

    # start at a random position
    center = driver.get_configuration(manipulator.random())
    yield center
   
    # initial step size is arbitrary
    step_size = 0.2

    while True:
      points = list()
      for param in manipulator.parameters(center.data):
        if param.is_primitive():
          
          unit_value = param.get_unit_value(center.data)

          if unit_value > 0.0:
            
            down_cfg = manipulator.copy(center.data)
            param.set_unit_value(down_cfg, max(0.0, unit_value - step_size))
            down_cfg = driver.get_configuration(down_cfg)
            yield down_cfg
            points.append(down_cfg)

          if unit_value < 1.0:
           
            up_cfg = manipulator.copy(center.data)
            param.set_unit_value(up_cfg, min(1.0, unit_value + step_size))
            up_cfg = driver.get_configuration(up_cfg)
            yield up_cfg
            points.append(up_cfg)

    
      points.sort(key=cmp_to_key(objective.compare))

      if objective.lt(points[0], center):
        
        center = points[0]
      else:
        
        step_size /= 2.0

# register our new technique in global list
technique.register(BasicPatternSearch())