# import adddeps  # fix sys.path

import opentuner
from opentuner import ConfigurationManipulator
from opentuner import IntegerParameter,EnumParameter
from opentuner import MeasurementInterface
from opentuner import Result


class GccFlagsTuner(MeasurementInterface):

  def manipulator(self):
    """
    Define the search space by creating a
    ConfigurationManipulator
    """
    manipulator = ConfigurationManipulator()
    # manipulator.add_parameter(
    #   IntegerParameter('blockSize', 8,16))
    manipulator.add_parameter(
      IntegerParameter('opt_level', 0,3))
    manipulator.add_parameter(
      EnumParameter('blockSize',['8','16','32','64','128'])
    )
    return manipulator

  def run(self, desired_result, input, limit):
    """
    Compile and run a given configuration then
    return performance
    """
    cfg = desired_result.configuration.data

    gcc_cmd = 'g++ Matrix_Multiplication.cpp '
    gcc_cmd += '-DBLOCK_SIZE='+ str(cfg['blockSize'])
    gcc_cmd += ' -O{0}'.format(cfg['opt_level'])
   
    gcc_cmd += ' -o ./tmp.bin'

    compile_result = self.call_program(gcc_cmd)
    # assert compile_result['returncode'] == 0

    run_cmd = './tmp.bin'

    run_result = self.call_program(run_cmd)
    # assert run_result['returncode'] == 0

    return Result(time=run_result['time'])

  def save_final_config(self, configuration):
    """called at the end of tuning"""
    print("Optimal block size written to mmm_final_config.json:", configuration.data)
    self.manipulator().save_to_file(configuration.data,
                                    'mmm_final_config.json')


if __name__ == '__main__':
  argparser = opentuner.default_argparser()
  GccFlagsTuner.main(argparser.parse_args())