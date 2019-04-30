package agh.heart.model;

import android.support.annotation.NonNull;

import heart.Configuration;
import heart.HeaRT;
import heart.State;
import heart.StateElement;
import heart.alsvfd.SimpleSymbolic;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.BuilderException;
import heart.exceptions.ModelBuildingException;
import heart.exceptions.NotInTheDomainException;
import heart.exceptions.ParsingSyntaxException;
import heart.parser.hmr.HMRParser;
import heart.parser.hmr.runtime.SourceString;
import heart.xtt.XTTModel;

public class ModelWrapper {
    private XTTModel model;
    private State state = new State();

    public ModelWrapper() {
        setupModel();
    }

    public ModelWrapper(String modelSrc) {
        setupModel(modelSrc);
    }

    public void runInference() {
        executeInference(state);
        state = HeaRT.getWm().getCurrentState(model);
    }

    private void executeInference(State state) {
        try {
            Configuration build = new Configuration.Builder()
                    .setInitialState(state)
                    .build();
            HeaRT.goalDrivenInference(
                    model,
                    new String[]{"wantsBlueTooth", "howareyouAction"},
                    build);
        } catch (BuilderException | NotInTheDomainException | AttributeNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    private void setupModel() {
        setupModel(null);
    }

    private void setupModel(String modelSrc) {
        if (modelSrc == null)
        {
            modelSrc = defaultSrc;
        }
        try {
            HMRParser parser = new HMRParser();
            parser.parse(new SourceString(modelSrc));
            model = parser.getModel();
        } catch (ParsingSyntaxException | ModelBuildingException e) {
            e.printStackTrace();
        }
    }

    private static final String defaultSrc = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TYPES DEFINITIONS %%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
            "\n" +
            "xtype [name: 'mA_type',\n" +
            "    domain: [-100 to 100],\n" +
            "    desc: 'type associated with miliampere',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'ms2_type',\n" +
            "    domain: [-100 to 100],\n" +
            "    desc: 'squared meters per seconds',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'uT_type',\n" +
            "    domain: [-100 to 100],\n" +
            "    desc: 'micro tesla',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'lux_type',\n" +
            "    domain: [-100 to 100],\n" +
            "    desc: 'lux unit',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'rads_type',\n" +
            "    domain: [-100 to 100],\n" +
            "    desc: 'radians per second',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 't_double',\n" +
            "    domain: [-100 to 100],\n" +
            "    scale: 3,\n" +
            "    desc: 'type associated with floating point numbers',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'int',\n" +
            "    domain: [-2147483647 to 2147483647],\n" +
            "    desc: 'Integer number',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'double',\n" +
            "    domain: [-100 to 100],\n" +
            "    desc: 'floating point number',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'longitude_type',\n" +
            "    domain: [-100 to 100],\n" +
            "    desc: 'position in horizontal dimension',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'latitude_type',\n" +
            "    domain: [-100 to 100],\n" +
            "    desc: 'position in vertical dimension',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'bool',\n" +
            "    domain: [false/0,true/1],\n" +
            "    desc: 'logic variable type',\n" +
            "    base: symbolic\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'id_type',\n" +
            "    domain: [0 to 1000],\n" +
            "    desc: 'some identifier',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'timestamp_type',\n" +
            "    domain: [-100 to 100],\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'range_type',\n" +
            "    domain: [0 to 1000],\n" +
            "    desc: 'variety of ranges',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'delay_type',\n" +
            "    domain: [0 to 1000],\n" +
            "    desc: 'time diff',\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'string',\n" +
            "    domain: ['a','b','c'],\n" +
            "    desc: 'string',\n" +
            "    ordered: no,\n" +
            "    base: symbolic\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'resolution_type',\n" +
            "    domain: [0 to 1000],\n" +
            "    base: numeric\n" +
            "    ].\n" +
            "\n" +
            "xtype [name: 'screen_status_type',\n" +
            "    domain: [off/0,on/1,locked/2,unlocked/3],\n" +
            "    base: symbolic\n" +
            "    ].\n" +
            "\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%% ATTRIBUTES DEFINITIONS %%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
            "\n" +
            "xattr [name: 'latitude',\n" +
            "    type: 'latitude_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    callback: 'agh.heart.callbacks.Location',\n" +
            "    abbrev: 'lat',\n" +
            "    desc: 'vertical position attribute'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'longitude',\n" +
            "    type: 'longitude_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    callback: 'agh.heart.callbacks.Location',\n" +
            "    abbrev: 'lon',\n" +
            "    desc: 'horizontal position attribute'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'inAGH',\n" +
            "    type: 'bool',\n" +
            "    class: simple,\n" +
            "    comm: out,\n" +
            "    abbrev: 'ina',\n" +
            "    desc: 'shows if someone is in AGH area'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accdevid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_max_range',\n" +
            "    type: 'range_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accmaxr'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_min_delay',\n" +
            "    type: 'delay_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accmindl'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_name',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accname'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_power',\n" +
            "    type: 'mA_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accP'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_resolution',\n" +
            "    type: 'resolution_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accres'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_type',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'acct'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_vendor',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accven'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_version',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accver'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_res_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_res_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accdts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_res_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accddevid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_res_1',\n" +
            "    type: 'ms2_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accd1'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_res_2',\n" +
            "    type: 'ms2_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accd2'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_res_3',\n" +
            "    type: 'ms2_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accd3'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_res_accuracy',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'accacc'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'accelerometer_res_label',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'acclabel'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccdevid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_max_range',\n" +
            "    type: 'range_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccmaxr'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_min_delay',\n" +
            "    type: 'delay_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccmindl'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_name',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccname'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_power',\n" +
            "    type: 'mA_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccP'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_resolution',\n" +
            "    type: 'resolution_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccres'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_type',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'lacct'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_vendor',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccven'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_version',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccver'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_res_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_res_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccdts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_res_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccddevid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_res_1',\n" +
            "    type: 'ms2_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccd1'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_res_2',\n" +
            "    type: 'ms2_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccd2'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_res_3',\n" +
            "    type: 'ms2_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccd3'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_res_accuracy',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'laccacc'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'laccelerometer_res_label',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'lacclabel'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_max_range',\n" +
            "    type: 'range_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magmr'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_min_delay',\n" +
            "    type: 'delay_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magmd'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_name',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magnm'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_power',\n" +
            "    type: 'mA_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magpw'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_resolution',\n" +
            "    type: 'resolution_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magres'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_type',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magtp'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_vendor',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magven'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_version',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magver'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_res_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magrid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_res_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magrts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_res_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magrdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_res_1',\n" +
            "    type: 'uT_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'mag1'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_res_2',\n" +
            "    type: 'uT_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'mag2'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_res_3',\n" +
            "    type: 'uT_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'mag3'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_res_accuracy',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magra'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'magnetometer_res_label',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'magrlb'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_max_range',\n" +
            "    type: 'range_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrmr'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_min_delay',\n" +
            "    type: 'delay_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrmd'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_name',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrnm'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_power',\n" +
            "    type: 'mA_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrpw'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_resolution',\n" +
            "    type: 'resolution_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrres'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_type',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrtp'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_vendor',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrven'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_version',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrver'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_res_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrrid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_res_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrrts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_res_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrrdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_res_x',\n" +
            "    type: 'rads_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    callback: 'agh.heart.callbacks.Gyroscope',\n" +
            "    abbrev: 'gyrx'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_res_y',\n" +
            "    type: 'rads_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    callback: 'agh.heart.callbacks.Gyroscope',\n" +
            "    abbrev: 'gyry'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_res_z',\n" +
            "    type: 'rads_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    callback: 'agh.heart.callbacks.Gyroscope',\n" +
            "    abbrev: 'gyrz'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_res_accuracy',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrra'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'gyroscope_res_label',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'gyrrl'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_max_range',\n" +
            "    type: 'range_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltmr'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_min_delay',\n" +
            "    type: 'delay_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltmd'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_name',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltnm'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_power',\n" +
            "    type: 'mA_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltpow'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_resolution',\n" +
            "    type: 'resolution_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltres'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_type',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'lttp'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_vendor',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltven'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_version',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltver'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_res_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltrid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_res_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltrts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_res_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltrdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_res',\n" +
            "    type: 'lux_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltr'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_res_accuracy',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltra'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'light_res_label',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'ltrl'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_status',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btstat'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_level',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btlvl'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_scale',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btscl'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_voltage',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btvol'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_temperature',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'bttemp'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_adaptor',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btadpt'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_health',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'bthlth'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_technology',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'bttech'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_discharge_res_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btdri'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_discharge_res_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btdrts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_discharge_res_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btdrdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_discharge_start',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btds'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_discharge_res_end',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btdre'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_discharge_res_end_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btdrets'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_charge_res_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btcri'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_charge_res_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btcrt'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_charge_res_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btcrdi'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_charge_start',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btcs'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_charge_res_end',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btcre'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'battery_charge_res_end_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'btcrets'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'locid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'locts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'locdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_latitude',\n" +
            "    type: 'latitude_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'loclat'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_longitude',\n" +
            "    type: 'longitude_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'loclong'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_bearing',\n" +
            "    type: 'double',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'locbr'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_speed',\n" +
            "    type: 'double',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'locspd'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_altitude',\n" +
            "    type: 'double',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'localt'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_provider',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'locprv'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_accuracy',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'loccacc'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'locations_label',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'loclab'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netdvid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_type',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'nettype'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_subtype',\n" +
            "    type: 'string',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netstype'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_state',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netstate'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_data_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_data_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netdts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_data_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netddi'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_data_type',\n" +
            "    type: 'int',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netdt'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_data_received_bytes',\n" +
            "    type: 'double',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netdrb'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_data_sent_bytes',\n" +
            "    type: 'double',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netdsb'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_data_received_packets',\n" +
            "    type: 'double',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netdrp'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'network_data_sent_packets',\n" +
            "    type: 'double',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'netdsp'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'screen_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'scrid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'screen_timestamp',\n" +
            "    type: 'timestamp_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'scrts'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'screen_dev_id',\n" +
            "    type: 'id_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    abbrev: 'scrdid'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'screen_status',\n" +
            "    type: 'screen_status_type',\n" +
            "    class: simple,\n" +
            "    comm: in,\n" +
            "    callback: 'agh.heart.callbacks.Screen',\n" +
            "    abbrev: 'scrstat'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'wants_bluetooth',\n" +
            "    type: 'bool',\n" +
            "    class: simple,\n" +
            "    comm: out,\n" +
            "    desc: 'Guess if user wants BlueTooth',\n" +
            "    abbrev: 'wntbt'\n" +
            "    ].\n" +
            "\n" +
            "xattr [name: 'watching_screen',\n" +
            "    type: 'bool',\n" +
            "    class: simple,\n" +
            "    comm: out,\n" +
            "    desc: 'Guess if user looks at screen',\n" +
            "    abbrev: 'wtchs'\n" +
            "    ].\n" +
            "\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%% TABLE SCHEMAS DEFINITIONS %%%%%%%%%%%%%%%%%%%%%%%%\n" +
            "\n" +
            "%! Position: 202,334\n" +
            "xschm 'InAGH': [longitude,latitude] ==> [inAGH].\n" +
            "%! Position: 91,133\n" +
            "xschm 'WatchingScreen': [screen_status,gyroscope_res_x,gyroscope_res_z] ==> [watching_screen].\n" +
            "%! Position: 918,363\n" +
            "xschm wantsBlueTooth: [watching_screen, inAGH] ==> [wants_bluetooth].\n" +
            "\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%% RULES DEFINITIONS %%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
            "\n" +
            "xrule 'InAGH'/0:\n" +
            "    [\n" +
            "        longitude in [19.91432 to 19.92381],\n" +
            "        latitude in [50.06398 to 50.06780]\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        inAGH set true\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #0.7\n" +
            "\n" +
            "xrule 'InAGH'/1:\n" +
            "    [\n" +
            "        longitude notin [19.91432 to 19.92381],\n" +
            "        latitude eq any\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        inAGH set false\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #1\n" +
            "\n" +
            "xrule 'InAGH'/2:\n" +
            "    [\n" +
            "        longitude in [0 to 100],\n" +
            "        latitude notin [50.06398 to 50.06780]\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        inAGH set false\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #1\n" +
            "\n" +
            "xrule 'InAGH'/6:\n" +
            "    [\n" +
            "        longitude in [19.91532 to 19.92281],\n" +
            "        latitude in [50.06420 to 50.06760]\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        inAGH set true\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #1\n" +
            "\n" +
            "xrule 'WatchingScreen'/3:\n" +
            "    [\n" +
            "        screen_status in [on,unlocked],\n" +
            "        gyroscope_res_x in [-30.00 to 30.00],\n" +
            "        gyroscope_res_z in [-90 to 0]\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        watching_screen set true\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #1\n" +
            "\n" +
            "xrule 'WatchingScreen'/4:\n" +
            "    [\n" +
            "        screen_status in [on,unlocked],\n" +
            "        gyroscope_res_x in [-60.0 to 60],\n" +
            "        gyroscope_res_z in [-100 to 30]\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        watching_screen set true\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #0.6\n" +
            "\n" +
            "xrule 'WatchingScreen'/5:\n" +
            "    [\n" +
            "        screen_status notin [on,unlocked],\n" +
            "        gyroscope_res_x eq any,\n" +
            "        gyroscope_res_z eq any\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        watching_screen set false\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #1\n" +
            "\n" +
            "xrule 'WatchingScreen'/10:\n" +
            "    [\n" +
            "        screen_status eq any,\n" +
            "        gyroscope_res_x notin [-60.0 to 60],\n" +
            "        gyroscope_res_z eq any\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        watching_screen set false\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #1\n" +
            "\n" +
            "xrule 'WatchingScreen'/11:\n" +
            "    [\n" +
            "        screen_status eq any,\n" +
            "        gyroscope_res_x eq any,\n" +
            "        gyroscope_res_z notin [-100 to 30]\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        watching_screen set false\n" +
            "    ]\n" +
            "    :[\n" +
            "        wantsBlueTooth\n" +
            "    ].\n" +
            "    #1\n" +
            "\n" +
            "xrule wantsBlueTooth/7:\n" +
            "    [\n" +
            "        inAGH eq true,\n" +
            "        watching_screen eq true\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        wants_bluetooth set true\n" +
            "    ]**>\n" +
            "        [\n" +
            "            'agh.heart.actions.HowAreYou_StartQuestionColor'\n" +
            "        ].\n" +
            "    #1\n" +
            "\n" +
            "xrule wantsBlueTooth/8:\n" +
            "    [\n" +
            "        inAGH eq false,\n" +
            "        watching_screen eq any\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        wants_bluetooth set false\n" +
            "    ]**>\n" +
            "        [\n" +
            "            'agh.heart.actions.HowAreYou_StartQuestionColor'\n" +
            "        ].\n" +
            "    #1\n" +
            "\n" +
            "xrule wantsBlueTooth/9:\n" +
            "    [\n" +
            "        inAGH eq any,\n" +
            "        watching_screen eq false\n" +
            "    ]\n" +
            "    ==>\n" +
            "    [\n" +
            "        wants_bluetooth set false\n" +
            "    ]**>\n" +
            "        [\n" +
            "            'agh.heart.actions.HowAreYou_StartQuestionColor'\n" +
            "        ].\n" +
            "    #1\n" +
            "\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
            "% File generated by XTT2 Web Editor\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n";
}
