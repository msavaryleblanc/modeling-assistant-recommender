package fr.shift.modeling.backend.domain.evaluator;

/*
 * This file is part of the Modeling Assistant Recommender. Author: Maxime Savary-Leblanc
 * The Modeling Assistant Recommender is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * The Modeling Assistant Recommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with The Modeling Assistant Recommender. If not, see <https://www.gnu.org/licenses/>.
 */

import fr.shift.modeling.backend.controller.entity.RecommendationItem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static fr.shift.modeling.backend.domain.evaluator.Context.*;

/**
 * Abstract definition of the AttributeContextEvaluator.
 * These objects are used to aggregate individual scores into one final confidence score for Attributes recommendations.
 */
@Service
public abstract class ContextEvaluator {

    public abstract List<String> getAllScoreNames();

    public Context getContext(List<String> attributeNameList,
                              List<String> classNameList) {
        if (attributeNameList.isEmpty() && classNameList.isEmpty()) {
            return CONTEXT_1;
        } else if (classNameList.isEmpty()) {
            return CONTEXT_2;
        } else if (attributeNameList.isEmpty()) {
            return CONTEXT_3;
        } else {
            return CONTEXT_4;
        }
    }

    public abstract HashMap<String, Double> getInvolvedScores(Context context);

    public abstract Double getConfidenceScore(RecommendationItem recommendationItem,
                                   List<String> attributeNameList,
                                   List<String> classNameList);

}
